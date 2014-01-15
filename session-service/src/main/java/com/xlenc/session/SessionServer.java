package com.xlenc.session;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * User: Michael Williams
 * Date: 1/13/14
 * Time: 9:57 PM
 */
public class SessionServer  extends Service<SessionConfiguration> {

    @Override
    public void initialize(Bootstrap<SessionConfiguration> bootstrap) {

    }

    @Override
    public void run(SessionConfiguration configuration, Environment environment) throws Exception {
        final MongoDatabaseConfiguration mongoDatabaseConfiguration = configuration.getMongoDatabaseConfiguration();
        final String host = mongoDatabaseConfiguration.getHost();
        final int port = mongoDatabaseConfiguration.getPort();
        final Mongo mongo = new MongoClient(host, port);
        final Morphia morphia = new Morphia();
        final String databaseName = mongoDatabaseConfiguration.getDatabaseName();

        wireSessionService(environment, mongo, morphia, databaseName);
        wireHealthChecks(environment, mongo);

    }

    private void wireHealthChecks(Environment environment, Mongo mongo) {
    }

    private void wireSessionService(Environment environment, Mongo mongo, Morphia morphia, String databaseName) {
    }

}
