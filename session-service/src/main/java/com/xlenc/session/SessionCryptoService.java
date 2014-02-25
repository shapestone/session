package com.xlenc.session;

import com.xlenc.api.session.Result;
import com.xlenc.api.session.ResultError;
import com.xlenc.api.session.SessionData;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.math.BigInteger;

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
        final Result<PublicKey, ResultError> readFileResult = PublicKeyReader.get(publicKeyFileName);
        if (readFileResult.isSuccess()) {
            final Result<String, ResultError> buildDataResult = buildData(sessionData);
            if (buildDataResult.isSuccess()) {
                final String data = buildDataResult.getData();
                final Result<byte[], ResultError> encryptDataResult = encryptData(data);
                final String base64Token = printBase64Binary(encryptDataResult.getData());
                sessionData.setToken(base64Token);
                createToken.setData(sessionData);
                createToken.setSuccess(true);
            } else {

            }
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

        try {
            final Result<PublicKey, ResultError> readPublicKeyResult = PublicKeyReader.get(publicKeyFileName);
            if (readPublicKeyResult.isSuccess()) {
                final PublicKey pubKey = readPublicKeyResult.getData();
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, pubKey);
                final byte[] encryptedData = cipher.doFinal(dataToEncrypt);
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

    /**
     * Encrypt Data
     * @param data
     * @throws IOException
     */
    private void decryptData(byte[] data) throws IOException {
        System.out.println("\n----------------DECRYPTION STARTED------------");
        byte[] descryptedData = null;

        try {
            final Result<PrivateKey, ResultError> privateKeyResultErrorResult = PrivateKeyReader.get(privateKeyFileName);
            PrivateKey privateKey = privateKeyResultErrorResult.getData();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            descryptedData = cipher.doFinal(data);
            System.out.println("Decrypted Data: " + new String(descryptedData));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("----------------DECRYPTION COMPLETED------------");
    }

    private Result<String, ResultError> buildData(SessionData sessionData) {
        final Result<String, ResultError> buildDataResult = new Result<>(false);
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new SessionDataModule());
            final String json = mapper.writeValueAsString(sessionData);
            buildDataResult.setSuccess(true);
            buildDataResult.setData(json);
        } catch (IOException e) {
            log.error("Exception Caught", e);
            buildDataResult.setError(new ResultError(e.getMessage(), e));
        }
        return buildDataResult;
    }

    public static class PublicKeyReader {
        public static Result<PublicKey, ResultError> get(String filename) {
            final Result<PublicKey, ResultError> publicKeyResult = new Result<>(false);

            final PublicKey publicKey;
            final File f = new File(filename);
            try (final FileInputStream fis = new FileInputStream(f);
                 final DataInputStream dis = new DataInputStream(fis)) {

                final byte[] keyBytes = new byte[(int)f.length()];
                dis.readFully(keyBytes);
                dis.close();

                final X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                final KeyFactory kf = KeyFactory.getInstance("RSA");
                publicKey = kf.generatePublic(spec);

                publicKeyResult.setData(publicKey);
                publicKeyResult.setSuccess(true);

            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                log.error("Exception Caught", e);
                publicKeyResult.setError(new ResultError(e.getMessage(), e));
            }
            return publicKeyResult;
        }
    }

    public static class PrivateKeyReader {
        public static Result<PrivateKey, ResultError> get(String filename) {
            final Result<PrivateKey, ResultError> publicKeyResult = new Result<>(false);

            final PrivateKey privateKey;
            final File f = new File(filename);

            try (final FileInputStream fis = new FileInputStream(f);
                 final DataInputStream dis = new DataInputStream(fis)) {

                final byte[] keyBytes = new byte[(int)f.length()];
                dis.readFully(keyBytes);
                dis.close();

                final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                final KeyFactory kf = KeyFactory.getInstance("RSA");
                privateKey = kf.generatePrivate(spec);

                publicKeyResult.setSuccess(true);
                publicKeyResult.setData(privateKey);

            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                log.error("Exception Caught", e);
                publicKeyResult.setError(new ResultError(e.getMessage(), e));
            }
            return publicKeyResult;
        }
    }

    public class SessionDataModule extends SimpleModule {
        public SessionDataModule() {
            super("SessionDataModule", new Version(0,0,1,null));
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(SessionData.class, SessionDataMixIn.class);
            // and other set up, if any
        }
    }

    public interface SessionDataMixIn {
        @JsonProperty("id")
        String getId();
        @JsonProperty("party_id")
        String getPartyId();
        @JsonProperty("application_id")
        String getApplicationId();
        @JsonProperty("created_on")
        Long getCreatedOn();
        @JsonProperty("last_active_on")
        Long getLastActiveOn();
        @JsonProperty("expired_on")
        Long getExpired();
        @JsonProperty("token")
        String getToken();
    }

}
