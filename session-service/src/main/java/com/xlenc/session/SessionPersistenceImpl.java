package com.xlenc.session;

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import org.bson.types.ObjectId;


/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public class SessionPersistenceImpl extends BasicDAO<SessionData, ObjectId> implements SessionPersistence {

    protected SessionPersistenceImpl(Mongo mongo, Morphia morphia, String dbName) {
        super(mongo, morphia, dbName);
    }

    public Result<SessionData, ResultError> saveSession(SessionData sessionData) {
        sessionData.setId(new ObjectId().toString());
        super.save(sessionData);
        return null;
    }

    public Result<SessionData, ResultError> findSession(final String id) {
//        return super.findOne(Mapper.ID_KEY, id);
        return null;
    }

    public int updateSession(String id, long version, final SessionData sessionData) {
        Query<SessionData> query = createQuery();
        query = query.field(Mapper.ID_KEY).equal(id);
        query = query.field("version").equal(version);

        final SessionUpdateOperations<SessionData> updateOperations = new SessionUpdateOperations<SessionData>(
                SessionData.class, ((DatastoreImpl)getDatastore()).getMapper(), sessionData
        );

        final UpdateResults<SessionData> update = updateFirst(query, updateOperations);
        return update.getUpdatedCount();
    }

    @Override
    public Result<SessionData, ResultError> updateSession(SessionData sessionData) {
        return null;
    }

    @Override
    public Result<SessionData, ResultError> endSession(SessionData sessionData) {
        return null;
    }


    public int deleteSession(String id) {
        final SessionData credential = new SessionData(id);
        final WriteResult delete = super.delete(credential);
        return delete.getN();
    }

}
