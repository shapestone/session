package com.xlenc.session;

import com.xlenc.api.session.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: Michael Williams
 * Date: 2/23/14
 * Time: 12:45 AM
 */
public class SessionCryptoServiceTest {

    private SessionCryptoService sessionCryptoService;

    @BeforeClass
    public void setUp() {
        final String publicKey = "/Users/mwilliams/IdeaProjects/session/session-service/src/test/resources/public_key.der";
        final String privateKey = "/Users/mwilliams/IdeaProjects/session/session-service/src/test/resources/private_key.der";
        this.sessionCryptoService = new SessionCryptoService(publicKey, privateKey);
    }

    @Test
    public void testEncryptSession() {
        final SessionData sessionData = new SessionData();
        final Result<SessionData,ResultError> token = this.sessionCryptoService.createToken(sessionData);
        assertTrue(token.isSuccess());
    }

}
