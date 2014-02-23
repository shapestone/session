package com.xlenc.session;

import com.xlenc.api.session.SessionData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * User: Michael Williams
 * Date: 2/23/14
 * Time: 12:45 AM
 */
public class SessionCryptoServiceTest {

    private SessionCryptoService sessionCryptoService;

    @BeforeClass
    public void setUp() {
        this.sessionCryptoService = new SessionCryptoService();
    }

    @Test
    public void testEncryptSession() {
        final SessionData sessionData = new SessionData();
        this.sessionCryptoService.createToken(sessionData);
    }
}
