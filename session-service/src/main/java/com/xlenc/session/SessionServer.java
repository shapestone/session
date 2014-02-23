package com.xlenc.session;

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

        final CassandraConfiguration cassandraConfiguration = configuration.getCassandraConfiguration();
        final String node = cassandraConfiguration.getHost();
        final String keySpaceName = cassandraConfiguration.getKeySpaceName();
        final String tableName = cassandraConfiguration.getTableName();
        final SessionPersistence sessionPersistence = new CassandraPersistence(node, keySpaceName, tableName);

        final CryptoConfiguration cryptoConfiguration = configuration.getCryptoConfiguration();
        final String publicKeyFileName = cryptoConfiguration.getPublicKeyFileName();
        final String privateKeyFileName = cryptoConfiguration.getPrivateKeyFileName();
        final SessionCryptoService sessionCryptoService = new SessionCryptoService(publicKeyFileName, privateKeyFileName);

        wireSessionService(environment, sessionPersistence, sessionCryptoService);
        wireHealthChecks(environment, sessionPersistence);
    }

    private void wireHealthChecks(Environment environment, SessionPersistence sessionPersistence) {
        environment.addHealthCheck(new CassandraHealthCheck(sessionPersistence));
    }

    private void wireSessionService(Environment environment, SessionPersistence sessionPersistence, SessionCryptoService sessionCryptoService) {
        final SessionService sessionService = new SessionServiceImpl(sessionPersistence, sessionCryptoService);
        final SessionResource sessionResource = new SessionResource(sessionService);
        environment.addResource(sessionResource);
    }

    public static void main(String[] args) throws Exception {
        new SessionServer().run(args);
    }

}
