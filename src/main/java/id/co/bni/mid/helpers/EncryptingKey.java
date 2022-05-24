package id.co.bni.mid.helpers;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class EncryptingKey {

    private static Logger logger = LoggerFactory.getLogger(EncryptingKey.class);

    private static final String key = "3crIpt9IcK5vv7Cc";
    private static final String initVector = "h@k1K7aB2eE1#Lo0";

    public String encrypt(String input) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encypted = cipher.doFinal(input.getBytes());
            return Base64.encodeBase64String(encypted);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        return null;
    }
    public String decrypt(String encypted){
        try{
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encypted));
            return new String(original);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
