/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author ThanhF
 */
public class EncryptUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static String hmacSHA(String secretKey, String data, String algorithm) {
        try {
            if (secretKey == null || data == null) {
                throw new NullPointerException();
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] rawHmac = mac.doFinal(dataBytes);
            int capacity = rawHmac.length * 2;
            StringBuilder sb = new StringBuilder(capacity);
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return "";
        }
    }

    public static String hmacSHA256(String secretKey, String data) {
        return hmacSHA(secretKey, data, HMAC_SHA256);
    }

    public static String hmacSHA512(String secretKey, String data) {
        return hmacSHA(secretKey, data, HMAC_SHA512);
    }

}
