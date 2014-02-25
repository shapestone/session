package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public class SessionServiceImpl implements SessionService {

    private final SessionPersistence sessionPersistence;
    private final SessionCryptoService sessionCryptoService;

    public SessionServiceImpl(SessionPersistence sessionPersistence, SessionCryptoService sessionCryptoService) {
        this.sessionPersistence = sessionPersistence;
        this.sessionCryptoService = sessionCryptoService;
    }

    public Result<SessionData, ResultError> createSession(SessionData sessionData) {
        Result<SessionData, ResultError> createSessionResult = new Result<>(false);
        sessionData.setCreatedOn(System.currentTimeMillis());
        sessionData.setLastActiveOn(System.currentTimeMillis());
        final Result<SessionData, ResultError> saveResult = sessionPersistence.saveSession(sessionData);
        if (saveResult.isSuccess()) {
            final Result<SessionData, ResultError> tokenResult = sessionCryptoService.createToken(sessionData);
            if (tokenResult.isSuccess()) {
                createSessionResult.setSuccess(true);
                createSessionResult.setData(tokenResult.getData());
            } else {
                createSessionResult = tokenResult;
            }
        } else {
            createSessionResult = saveResult;
        }
        return createSessionResult;
    }

    public Result<SessionData, ResultError> readSession(String id) {
        return sessionPersistence.findSession(id);
    }

    public int deleteSession(String id) {
        return sessionPersistence.deleteSession(id);
    }

    @Override
    public Result<SessionData, ResultError> updateSession(SessionData sessionData) {
        sessionData.setLastActiveOn(System.currentTimeMillis());
        return sessionPersistence.updateSession(sessionData);
    }

    @Override
    public Result<SessionData, ResultError> expireSession(SessionData sessionData) {
        sessionData.setLastActiveOn(System.currentTimeMillis());
        sessionData.setExpiredOn(System.currentTimeMillis());
        return sessionPersistence.endSession(sessionData);
    }

    @Override
    public Result<SessionData, ResultError> validateSession(SessionData sessionData) {
        final Result<SessionData, ResultError> readResult = this.readSession(sessionData.getId());
        if (readResult.isSuccess() &&
            readResult.getData() == null ||
            readResult.getData().getId() == null) {
            readResult.setSuccess(false);
        }
        return readResult;
    }

}
