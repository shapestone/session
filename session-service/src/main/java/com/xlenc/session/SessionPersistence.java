package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:58 AM
 */
public interface SessionPersistence {
    Result<SessionData, ResultError> saveSession(SessionData sessionData);

    Result<SessionData, ResultError> findSession(String id);

    int deleteSession(String id);

    Result<SessionData,ResultError> updateSession(SessionData sessionData);

    Result<SessionData,ResultError> endSession(SessionData sessionData);
}
