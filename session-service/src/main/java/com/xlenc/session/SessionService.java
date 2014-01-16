package com.xlenc.session;

import com.xlenc.api.session.SessionData;

/**
 * User: Michael Williams
 * Date: 1/16/14
 * Time: 7:59 AM
 */
public interface SessionService {
    SessionData addSession(SessionData sessionData);

    SessionData readSession(String id);

    int deleteSession(String id);
}
