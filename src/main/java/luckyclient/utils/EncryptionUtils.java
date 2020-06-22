package luckyclient.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

/**
 * @author fengjian
 * @date 2020/6/22 22:17
 */
@Component
public class EncryptionUtils {

    private static String salt;

    private static String pass;

    @Value("${client.config.slat}")
    public  void setSalt(String salt) {
        EncryptionUtils.salt = salt;
    }

    @Value("${client.config.pass}")
    public void setPass(String pass) {
        EncryptionUtils.pass = pass;
    }

    /**
     * 加密
     * @param value
     * @return
     */
    public static String encrypt(String value) throws Exception {
        // 加密
        PBEKeySpec pbeKeySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
        Key key = factory.generateSecret(pbeKeySpec);
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt.getBytes(),100);
        Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
        cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
        byte[] result = cipher.doFinal(value.getBytes());
        System.out.println("jdk PBE encrypt: " + Base64.encodeBase64String(result));
        return Base64.encodeBase64String(result);
    }

    /**
     * 解密
     * @param value
     * @return
     * @throws Exception
     */
    public static String decrypt(String value)throws Exception
    {
        // 解密
        PBEKeySpec pbeKeySpec = new PBEKeySpec(pass.toCharArray());
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt.getBytes(),100);
        Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
        Key key = factory.generateSecret(pbeKeySpec);
        cipher.init(Cipher.DECRYPT_MODE, key,pbeParameterSpec);
        byte[] result = cipher.doFinal(Base64.decodeBase64(value));
        return  new String(result);
    }
}
