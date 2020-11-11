

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public abstract class EncryptionDecryption {


    public static  String encrypt(String plainText,String mode,String method,SecretKey secretKey) throws Exception{

        Cipher c = Cipher.getInstance(method+"/" + mode + "/PKCS5Padding");
        byte[] iv = new byte[c.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        c.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        return Base64.getEncoder().encodeToString(c.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String cipherText, String mode,String method,SecretKey secretKey) throws Exception{

        Cipher c = Cipher.getInstance(method+"/" + mode + "/PKCS5Padding");
        byte[] iv = new byte[c.getBlockSize()];
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        c.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        return new String(c.doFinal(Base64.getDecoder().decode(cipherText)));
    }
}
