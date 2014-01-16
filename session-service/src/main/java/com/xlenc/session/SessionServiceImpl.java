package com.xlenc.session;

import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public class SessionServiceImpl implements SessionService {

    private final SessionPersistence sessionPersistence;

    public SessionServiceImpl(SessionPersistence sessionPersistence) {
        this.sessionPersistence = sessionPersistence;
    }

    public SessionData addSession(SessionData sessionData) {
        return sessionPersistence.addSession(sessionData);
    }

    public SessionData readSession(String id) {
        return sessionPersistence.findSession(id);
    }

    public int deleteSession(String id) {
        return sessionPersistence.deleteSession(id);
    }

}
