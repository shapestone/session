package com.xlenc.session;

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
        final CassandraPersistence cassandraPersistence = new CassandraPersistence("localhost");
        final SessionService sessionService = new SessionServiceImpl(cassandraPersistence);
        this.sessionResource = new SessionResource(sessionService);
        this.sessionData = createSessionData();
    }

    private static SessionData createSessionData() {
        final SessionData sessionData = new SessionData(id, data);
        sessionData.setApplicationId("stack-up");
        sessionData.setPartyId("me");
        sessionData.setCreated(System.currentTimeMillis());
        sessionData.setLastRequest(System.currentTimeMillis());
        sessionData.setVersion((long) 1);
        sessionData.setData(new HashMap<String, Object>() {{
            put("mysession", "thx");
        }});
        return sessionData;
    }

    @Test
    public void testAddSession() {
        final Response response = sessionResource.addSession(sessionData);

        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, SessionError> sessionDataResult = (Result<SessionData, SessionError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());

    }

    @Test(dependsOnMethods = {"testAddSession"})
    public void testReadSession() {
        final Response response = sessionResource.readSession(sessionData.getId());
        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, SessionError> sessionDataResult = (Result<SessionData, SessionError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());
    }

    @Test(dependsOnMethods = {"testReadSession"})
    public void testUpdateSession() {
        sessionData.getData().put("more", "session data bro.");
        final Response response = sessionResource.updateSession(sessionData);
        final Object entity = response.getEntity();

        assertNotNull(entity);

        @SuppressWarnings("unchecked")
        final Result<SessionData, SessionError> sessionDataResult = (Result<SessionData, SessionError>) entity;

        assertTrue(sessionDataResult.isSuccess());

        this.sessionData = sessionDataResult.getData();

        assertNotNull(this.sessionData.getId());
    }



}
