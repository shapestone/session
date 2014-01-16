package com.xlenc.session;

import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.UpdateOpsImpl;
import com.mongodb.DBObject;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 8:46 AM
 */
public class SessionUpdateOperations<T> extends UpdateOpsImpl<T> {

    private Mapper mapper;
    private T t;
    public SessionUpdateOperations(Class<T> type, Mapper mapper, T t) {
        super(type, mapper);
        this.mapper = mapper;
        this.t = t;
    }

    @Override
    public DBObject getOps() {
        return mapper.toDBObject(t);
    }

}
