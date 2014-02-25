package com.xlenc.session;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.utils.UUIDs;
import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Michael Williams
 * Date: 2/6/14
 * Time: 11:31 PM
 */
public class CassandraPersistence implements SessionPersistence {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Session session;
    private String keySpaceName;
    private String tableName;
    private ObjectMapper mapper = new ObjectMapper();

    public CassandraPersistence(String node, String keySpaceName, String tableName) {
        this.keySpaceName = keySpaceName;
        this.tableName = tableName;
        final Cluster cluster = Cluster.builder().addContactPoint(node).build();
        session = cluster.connect();
        final Metadata metadata = cluster.getMetadata();
        log.info("Connected to cluster: {}", metadata.getClusterName());
        final String CREATE_KEYSPACE = getSessionKeyspaceCQL();
        final String CREATE_TABLE = getSessionTableCQL();
        if (!keyspaceExists(metadata)) {
            // create keyspace
            session.execute(CREATE_KEYSPACE);

            // create table
            session.execute(CREATE_TABLE);

            log.info("Created keyspace and table");
        } else if (!tableExists(metadata)) {
            // create table
            session.execute(CREATE_TABLE);

            log.info("Created table only");
        } else {
            // do nothing
            log.info("Keyspace and table creation thereof is not necessary.");
        }
    }

    private String getSessionKeyspaceCQL() {
        return "CREATE KEYSPACE sessiondb " +
               "WITH replication = {" +
               "'class':'SimpleStrategy', " +
               "'replication_factor':3" +
               "};";
    }

    private String getSessionTableCQL() {
        final String fullTableName = keySpaceName.trim().concat(".").concat(tableName.trim());
        return "CREATE TABLE " + fullTableName + " (" +
               "id uuid PRIMARY KEY, " +
               "party_id text, " +
               "application_id text, " +
               "created timestamp, " +
               "last_request timestamp, " +
               "ended timestamp, " +
               "data text" +
               ");";
    }

    private boolean keyspaceExists(Metadata metadata) {
        final KeyspaceMetadata keyspace = metadata.getKeyspace(keySpaceName);
        return keyspace != null;
    }

    private boolean tableExists(Metadata metadata) {
        final KeyspaceMetadata keyspace = metadata.getKeyspace(keySpaceName);
        final String TABLE_NAME = "sessions";
        final TableMetadata table = keyspace.getTable(TABLE_NAME);
        return table != null;
    }

    private Session getSession() {
        return this.session;
    }

    @Override
    public Result<SessionData, ResultError> saveSession(SessionData sessionData) {
        final Result<SessionData, ResultError> result = new Result<>(false, sessionData);
        try {
            final String query = getInsertCQL();
            final UUID id = UUIDs.random();
            log.debug("UUID {}", id);
            session.execute(
                    query,
                    id,
                    sessionData.getPartyId(),
                    sessionData.getApplicationId(),
                    sessionData.getCreatedOn(),
                    sessionData.getLastActiveOn(),
                    sessionData.getExpiredOn(),
                    toJson(sessionData.getData())
            );
            sessionData.setId(id.toString());
            result.setSuccess(true);
        } catch (IOException e) {
            log.error("Exception Caught: ", e.getMessage());
            final ResultError error = new ResultError(e.getMessage(), e);
            result.setError(error);
        }
        return result;
    }

    private String getInsertCQL() {
        return "INSERT INTO sessiondb.sessions " +
                "(id, party_id, application_id, created, last_request, ended, data) " +
                "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)";
    }

    public void close() {
        session.getCluster().shutdown();
    }

    @Override
    public Result<SessionData, ResultError> findSession(String id) {
        final SessionData sessionData = new SessionData(id);
        final Result<SessionData, ResultError> result = new Result<>(false, sessionData);
        try {
            final String query = "SELECT * FROM sessiondb.sessions WHERE id = ?";
            final Row row = session.execute(query, UUID.fromString(id)).one();
            if (row != null) {
                sessionData.setId(row.getUUID("id").toString());
                sessionData.setPartyId(row.getString("party_id"));
                sessionData.setApplicationId(row.getString("application_id"));
                sessionData.setCreatedOn(toTimeInMillis(row.getDate("created")));
                sessionData.setLastActiveOn(toTimeInMillis(row.getDate("last_request")));
                sessionData.setExpiredOn(toTimeInMillis(row.getDate("ended")));
                sessionData.setData(toMap(row.getString("data")));
            }
            result.setSuccess(true);
            result.setData(sessionData);
        } catch (IOException e) {
            log.error("Exception Caught: ", e.getMessage());
            final ResultError error = new ResultError(e.getMessage(), e);
            result.setError(error);
        }
        return result;
    }

    public Result<SessionData, ResultError> updateSession(SessionData sessionData) {
        //TODO: should we version the row and used the version as part of the where clause when updating
        //TODO: if we do what would be the proper way to handle updates where the expected version is incorrect
        final Result<SessionData, ResultError> result = new Result<>(false, sessionData);
        try {
            final String updateQuery = "UPDATE sessiondb.sessions SET last_request = ?, data = ? WHERE id = ?";
            UUID id = UUID.fromString(sessionData.getId());
            session.execute(
                updateQuery,
                sessionData.getLastActiveOn(),
                toJson(sessionData.getData()),
                id
            );
            result.setSuccess(true);
        } catch (IOException e) {
            log.error("Exception Caught: ", e.getMessage());
            final ResultError error = new ResultError(e.getMessage(), e);
            result.setError(error);
        }
        return result;
    }

    @Override
    public Result<SessionData, ResultError> endSession(SessionData sessionData) {
        final Result<SessionData, ResultError> result = new Result<>(false, sessionData);
        try {
            final String updateQuery = "UPDATE sessiondb.sessions SET last_request = ?, ended = ? WHERE id = ?";
            UUID id = UUID.fromString(sessionData.getId());
            session.execute(
                    updateQuery,
                    sessionData.getLastActiveOn(),
                    sessionData.getExpiredOn(),
                    id
            );
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("Exception Caught: ", e.getMessage());
            final ResultError error = new ResultError(e.getMessage(), e);
            result.setError(error);
        }
        return result;
    }

    private Long toTimeInMillis(Date date) {
        Long aLong;
        if (date == null) {
            aLong = null;
        } else {
            aLong = date.getTime();
        }

        return aLong;
    }

    private String toJson(Map<String, Object> data) throws IOException {
        return mapper.writeValueAsString(data);
    }

    private Map<String, Object> toMap(String data) throws IOException {
        return mapper.readValue(data, new TypeReference<HashMap<String, String>>() {});
    }

    @Override
    public int deleteSession(String id) {
        return 0;
    }

}
