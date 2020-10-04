package ng.min.gateway.utils;

import ng.min.gateway.dto.Response;
import ng.min.gateway.dto.ResponseData;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Aes encryption
 */
@Component
public class AES {
//    @Autowired
//    private Environment env;
    private static SecretKeySpec secretKey;
    private static String backendEncryptionKey;
    private static String frontendEncryptionKey;
    private static final Logger log = Logger.getLogger(AES.class.getName());

    //    private static String decryptedString;
//    private static String encryptedString;
//    public void setEncryptionKey(String encryptionKey) {
//        AES.backendEncryptionKey = encryptionKey;
//    }
//    public void setEncryptionKey(String encryptionKey) {
//        AES.backendEncryptionKey = encryptionKey;
//    }

    @Value("${app.key.encryption.backend}")
    public void setBackendEncryptionKey(String backendEncryptionKey) {
        AES.backendEncryptionKey = backendEncryptionKey;
    }

    @Value("${app.key.encryption.frontend}")
    public void setFrontendEncryptionKey(String frontendEncryptionKey) {
        AES.frontendEncryptionKey = frontendEncryptionKey;
    }

    public static String getBackendEncryptionKey() {
        return backendEncryptionKey;
    }

    public static String getFrontendEncryptionKey() {
        return frontendEncryptionKey;
    }

    private static void setKey(String myKey) {


        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");


        } catch (NoSuchAlgorithmException e) {
            log.info("Error while setting key >>>>>>>>>>" + e);
        }


    }

    public static String encrypt(String strToEncrypt, boolean isFront) {
        try {

            var encryptionKey = "";
            if (isFront)
                encryptionKey = getFrontendEncryptionKey();
            else encryptionKey = getBackendEncryptionKey();

            log.info("The Key used for encryption "+encryptionKey);
            log.info("String to be encrypted "+strToEncrypt);
            AES.setKey(encryptionKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);


            return Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));


        } catch (Exception e) {

            log.info("Error while encrypting >>>>>>>>>" + e);
            return null;
        }
    }
    public static String encryptBody(String strToEncrypt) {
        try {




//            log.info("String to be encrypted "+strToEncrypt);

            Response response = JsonConverter.getElement(strToEncrypt,Response.class);
//            log.info("Response Object "+response);
            var data = response.getData();
            if (data == null)
                return JsonConverter.getJson(response);
            //Extract token from the response body
            var tokenEncryptedWithBackendKey= data.getToken();
            if (!Validation.validData(tokenEncryptedWithBackendKey))
                return JsonConverter.getJson(response);
            // Decrypt backend token with backend key
            var jwtTokenExtractedWithBackendKey = decrypt(tokenEncryptedWithBackendKey,false);
//            log.info("JWT Token decrypted with backend key  "+jwtTokenExtractedWithBackendKey);
            //Encrypt the JWT token with frontend key
            var tokenEncryptedWithFrontEndKey = encrypt(jwtTokenExtractedWithBackendKey, true);
            //set the token in the response
//            log.info("Token encrypted with frontend "+tokenEncryptedWithFrontEndKey);

            response.setData(data.setToken(tokenEncryptedWithFrontEndKey));
//            log.info("Response Object Modified "+response);

            return JsonConverter.getJson(response);


        } catch (Exception e) {

            log.info("Error while encrypting >>>>>>>>>" + e);
            return null;
        }
    }

    public static String decrypt(String strToDecrypt, boolean isFront) {
        try {
            if (!Validation.validData(strToDecrypt)) {
                log.info("The data to be decoded is null ");
                return null;
            }
            var encryptionKey = "";
            if (isFront) {
                encryptionKey = getFrontendEncryptionKey();
            }
            else encryptionKey = getBackendEncryptionKey();

            log.info("The Key used "+encryptionKey);
            log.info("String to be decrypted "+strToDecrypt);
            if (!Validation.validData(encryptionKey)) {
                log.info("encryption key is null ");
                return null;
            }
            AES.setKey(encryptionKey);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));

        } catch (Exception e) {

            log.info("Error while decrypting: >>>>>>>>>" + e);
            return null;
        }
    }

    public static void main(String[] args) {

    }


}