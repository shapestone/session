package com.xlenc.session;

import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:58 AM
 */
public interface SessionPersistence {
    Result<SessionData, SessionError> addSession(SessionData sessionData);

    Result<SessionData, SessionError> findSession(String id);

    int deleteSession(String id);

    Result<SessionData,SessionError> updateSession(SessionData sessionData);
}
