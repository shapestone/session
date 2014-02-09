package com.xlenc.session;

import com.xlenc.api.session.SessionData;

import javax.ws.rs.core.Response;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public interface SessionService {
    Result<SessionData, SessionError> addSession(SessionData sessionData);

    Result<SessionData, SessionError> readSession(String id);

    int deleteSession(String id);

    Result<SessionData, SessionError> updateSession(SessionData sessionData);
}
