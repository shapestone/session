package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public interface SessionService {
    Result<SessionData, ResultError> createSession(SessionData sessionData);

    Result<SessionData, ResultError> readSession(String id);

    int deleteSession(String id);

    Result<SessionData, ResultError> updateSession(SessionData sessionData);

    Result<SessionData,ResultError> expireSession(SessionData sessionData);

    Result<SessionData,ResultError> validateSession(SessionData sessionData);
}
