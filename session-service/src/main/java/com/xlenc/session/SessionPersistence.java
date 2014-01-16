package com.xlenc.session;

import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:58 AM
 */
public interface SessionPersistence {
    SessionData addSession(SessionData sessionData);

    SessionData findSession(String id);

    int deleteSession(String id);
}
