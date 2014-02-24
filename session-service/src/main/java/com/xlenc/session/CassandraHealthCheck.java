package com.xlenc.session;

import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import com.yammer.metrics.core.HealthCheck;

import java.util.UUID;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:29 AM
 */
public class CassandraHealthCheck extends HealthCheck {

    private SessionPersistence sessionPersistence;

    public CassandraHealthCheck(SessionPersistence cassandraPersistence) {
        super("CassandraHealthCheck");
        this.sessionPersistence = cassandraPersistence;
    }

    @Override
    protected Result check() throws Exception {
        final com.xlenc.api.session.Result<SessionData,ResultError> session;
        session = sessionPersistence.findSession(UUID.randomUUID().toString());
        final Result healthy;
        if (session.isSuccess()) {
            healthy = Result.healthy();
        } else {
            healthy = Result.unhealthy(session.getError().getMessage());
        }
        return healthy;
    }

}
