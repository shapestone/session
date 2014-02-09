package com.xlenc.session;

import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
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
    private String KEY_SPACE_NAME = "sessiondb";
    private ObjectMapper mapper = new ObjectMapper();

    public CassandraPersistence(String node) {
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
        return "CREATE TABLE sessiondb.sessions (" +
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
        final KeyspaceMetadata keyspace = metadata.getKeyspace(KEY_SPACE_NAME);
        return keyspace != null;
    }

    private boolean tableExists(Metadata metadata) {
        final KeyspaceMetadata keyspace = metadata.getKeyspace(KEY_SPACE_NAME);
        final String TABLE_NAME = "sessions";
        final TableMetadata table = keyspace.getTable(TABLE_NAME);
        return table != null;
    }

    private Session getSession() {
        return this.session;
    }

    @Override
    public Result<SessionData, SessionError> addSession(SessionData sessionData) {
        final Result<SessionData, SessionError> result = new Result<>(false, sessionData);
        try {
            final String query = getInsertCQL();
            final UUID id = UUIDs.random();
            log.debug("UUID {}", id);
            session.execute(
                    query,
                    id,
                    sessionData.getPartyId(),
                    sessionData.getApplicationId(),
                    sessionData.getCreated(),
                    sessionData.getLastRequest(),
                    sessionData.getEnded(),
                    toJson(sessionData.getData())
            );
            sessionData.setId(id.toString());
            result.setSuccess(true);
        } catch (IOException e) {
            log.error("Exception Caught: ", e.getMessage());
            final SessionError error = new SessionError(e.getMessage(), e);
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

    private String toJson(Map<String, Object> data) throws IOException {
        return mapper.writeValueAsString(data);
    }

    public void close() {
        session.getCluster().shutdown();
    }

    @Override
    public Result<SessionData, SessionError> findSession(String id) {
        final SessionData sessionData = new SessionData(id);
        final Result<SessionData, SessionError> result = new Result<>(false, sessionData);
        try {
            final String query = "SELECT * FROM sessiondb.sessions WHERE id = ?";
            final Row row = session.execute(query, UUID.fromString(id)).one();
            if (row != null) {
                sessionData.setId(row.getUUID("id").toString());
                sessionData.setPartyId(row.getString("party_id"));
                sessionData.setApplicationId(row.getString("application_id"));
                sessionData.setCreated(toTimeInMillis(row.getDate("created")));
                sessionData.setLastRequest(toTimeInMillis(row.getDate("last_request")));
                sessionData.setEnded(toTimeInMillis(row.getDate("ended")));
                sessionData.setData(toMap(row.getString("data")));
            }
            result.setSuccess(true);
            result.setData(sessionData);
        } catch (IOException e) {
            log.error("Exception Caught: ", e.getMessage());
            final SessionError error = new SessionError(e.getMessage(), e);
            result.setError(error);
        }
        return result;
    }

    public Result<SessionData, SessionError> updateSession(SessionData sessionData) {
        //TODO: should we version the row and used the version as part of the where clause when updating
        //TODO: if we do what would be the proper way to handle updates where the expected version is incorrect
        final Result<SessionData, SessionError> result = new Result<>(false, sessionData);
        final String updateQuery = "UPDATE sessiondb.sessions SET last_request = ?, data = ? WHERE id = ?";
        session.execute(
            updateQuery,
            sessionData.getLastRequest(),
            sessionData.getData(),
            sessionData.getId()
        );
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

    private Map<String, Object> toMap(String data) throws IOException {
        return mapper.readValue(data, new TypeReference<HashMap<String, String>>() {});
    }

    @Override
    public int deleteSession(String id) {
        return 0;
    }

}
