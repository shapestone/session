package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.HashMap;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * User: Michael Williams
 * Date: 2/8/14
 * Time: 1:25 PM
 */
public class SessionResourceIT {

    private SessionResource sessionResource;
    private SessionData sessionData;

    @BeforeClass
    public void setUp() {
        final CassandraPersistence cassandraPersistence = new CassandraPersistence("localhost", "sessiondb", "sessions");
        final String publicKey = "/Users/mwilliams/IdeaProjects/session/session-service/src/test/resources/public_key.der";
        final String privateKey = "/Users/mwilliams/IdeaProjects/session/session-service/src/test/resources/private_key.der";
        final SessionCryptoService sessionCryptoService = new SessionCryptoService(publicKey, privateKey);
        final SessionService sessionService = new SessionServiceImpl(cassandraPersistence, sessionCryptoService);
        this.sessionResource = new SessionResource(sessionService);
        this.sessionData = createSessionData();
    }

    private static SessionData createSessionData() {
        final SessionData sessionData = new SessionData();
        sessionData.setApplicationId("stack-up");
        sessionData.setPartyId("me");
        sessionData.setCreatedOn(System.currentTimeMillis());
        sessionData.setLastActiveOn(System.currentTimeMillis());
        sessionData.setData(new HashMap<String, Object>() {{
            put("mysession", "thx");
        }});
        return sessionData;
    }

    @Test(enabled = false)
    public void testAddSession() {
        final Response response = sessionResource.addSession(sessionData);

        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, ResultError> sessionDataResult = (Result<SessionData, ResultError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());

    }

    @Test(dependsOnMethods = {"testAddSession"}, enabled = false)
    public void testReadSession() {
        final Response response = sessionResource.readSession(sessionData.getId());
        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, ResultError> sessionDataResult = (Result<SessionData, ResultError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        final SessionData data = sessionDataResult.getData();

        assertNotNull(data.getPartyId());
        assertNotNull(data.getApplicationId());
        assertNotNull(data.getCreatedOn());
        assertNotNull(data.getId());
        assertNotNull(data.getLastActiveOn());
        assertNotNull(data.getData());

        this.sessionData = data;

        assertNotNull(this.sessionData.getId());
    }

    @Test(dependsOnMethods = {"testReadSession"}, enabled = false)
    public void testUpdateSession() {
        sessionData.getData().put("more", "session data bro.");
        final Response response = sessionResource.updateSession(sessionData.getId(), sessionData.getData());
        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, ResultError> sessionDataResult = (Result<SessionData, ResultError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());
    }

    @Test(dependsOnMethods = {"testUpdateSession"}, enabled = false)
    public void testEndSession() {
        final Response response = sessionResource.expireSession(sessionData.getId());

        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, ResultError> sessionDataResult = (Result<SessionData, ResultError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());
    }

}
