package com.xlenc.session;

import com.datastax.driver.core.Session;
import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

/**
 * User: Michael Williams
 * Date: 2/22/14
 * Time: 11:44 PM
 */
public class SessionCryptoService {

    private static final Logger log = LoggerFactory.getLogger(SessionCryptoService.class.getName());
    private final String publicKeyFileName;
    private final String privateKeyFileName;

    public SessionCryptoService(String publicKeyFileName, String privateKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
        this.privateKeyFileName = privateKeyFileName;
    }

    public Result<SessionData, ResultError> createToken(SessionData sessionData) {
        final Result<SessionData, ResultError> createToken = new Result<>(false);
        final Result<PublicKey, ResultError> readFileResult = readPublicKeyFromFile(publicKeyFileName);
        if (readFileResult.isSuccess()) {
            final String data = buildData(sessionData);
            final Result<byte[], ResultError> encryptDataResult = encryptData(data);
            final String base64Token = printBase64Binary(encryptDataResult.getData());
            sessionData.setToken(base64Token);
            createToken.setData(sessionData);
            createToken.setSuccess(true);
        } else {
            createToken.setError(readFileResult.getError());
        }
        return createToken;
    }

    /**
     * Encrypt Data
     * @param data
     */
    private Result<byte[], ResultError> encryptData(String data) {
        log.trace("----------------DATA ENCRYPTION STARTED------------");
        final Result<byte[], ResultError> encryptDataResult = new Result<>(false);
        log.debug("Data Before Encryption : {}", data);
        final byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            final Result<PublicKey, ResultError> readPublicKeyResult = readPublicKeyFromFile(publicKeyFileName);
            if (readPublicKeyResult.isSuccess()) {
                final PublicKey pubKey = readPublicKeyResult.getData();
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                encryptedData = cipher.doFinal(dataToEncrypt);
                log.debug("Encryted Data: {}", encryptedData);
                encryptDataResult.setSuccess(true);
                encryptDataResult.setData(encryptedData);
            }
        } catch (Exception e) {
            log.error("Exception Caught", e);
            encryptDataResult.setError(new ResultError(e.getMessage(), e));;
        }
        log.trace("----------------DATA ENCRYPTION COMPLETED------------");
        return encryptDataResult;
    }

    private Result<String, ResultError> buildData(SessionData sessionData) {
        final Result<String, ResultError> buildDataResult = new Result<>(false);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String json = mapper.writeValueAsString(sessionData);
            mapper.registerModule();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildDataResult;
    }

    private Result<PublicKey, ResultError> readPublicKeyFromFile(String fileName) {
        final Result<PublicKey, ResultError> result = new Result<>(false);
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File(fileName));
            ois = new ObjectInputStream(fis);

            final BigInteger modulus = (BigInteger) ois.readObject();
            final BigInteger exponent = (BigInteger) ois.readObject();

            //Get Public Key
            final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);

            result.setSuccess(true);
            result.setData(publicKey);

        } catch (Exception e) {

            log.error("Exception Caught", e);
            result.setError(new ResultError(e.getMessage(), e));;

        } finally{
            if(ois != null){
                try {
                    ois.close();
                    fis.close();
                } catch (IOException e) {
                    log.error("Exception Caught", e);
                }
            }
        }

        return result;

    }

    public class MyModule extends SimpleModule {
        public MyModule() {
            super("ModuleName", new Version(0,0,1,null));
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(SessionData.class, SessionDataMixIn.class);
            // and other set up, if any
        }

    }

    public interface SessionDataMixIn {
        String getId();
        String getPartyId();
        String getApplicationId();
        Long getCreated();
        Long getLastActive();
        Long getExpired();
        String getToken();
    }

}
