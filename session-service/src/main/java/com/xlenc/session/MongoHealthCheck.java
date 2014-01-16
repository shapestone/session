package com.xlenc.session;

import com.mongodb.Mongo;
import com.yammer.metrics.core.HealthCheck;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:29 AM
 */
public class MongoHealthCheck extends HealthCheck {

    private final Mongo mongo;

    public MongoHealthCheck(Mongo mongo) {
        super("MongoHealthCheck");
        this.mongo = mongo;
    }

    @Override
    protected HealthCheck.Result check() throws Exception {
        mongo.getDatabaseNames();
        return HealthCheck.Result.healthy();
    }

}
