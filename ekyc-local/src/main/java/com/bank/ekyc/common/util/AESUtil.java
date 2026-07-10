package com.bank.ekyc.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private AESUtil() {
    }

    public static String encrypt(String plainText, String masterKey) {

        try {

            SecretKeySpec secretKey = buildSecretKey(masterKey);

            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] cipherText =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer =
                    ByteBuffer.allocate(iv.length + cipherText.length);

            buffer.put(iv);
            buffer.put(cipherText);

            return Base64.getEncoder()
                    .encodeToString(buffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Encrypt failed", e);
        }
    }

    public static String decrypt(String encryptedText, String masterKey) {

        try {

            byte[] data =
                    Base64.getDecoder().decode(encryptedText);

            ByteBuffer buffer =
                    ByteBuffer.wrap(data);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] cipherText =
                    new byte[buffer.remaining()];
            buffer.get(cipherText);

            SecretKeySpec secretKey =
                    buildSecretKey(masterKey);

            Cipher cipher =
                    Cipher.getInstance(TRANSFORMATION);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] plain =
                    cipher.doFinal(cipherText);

            return new String(
                    plain,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new RuntimeException("Decrypt failed", e);
        }
    }

    private static SecretKeySpec buildSecretKey(String masterKey)
            throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] key =
                digest.digest(masterKey.getBytes(StandardCharsets.UTF_8));

        return new SecretKeySpec(key, ALGORITHM);
    }
}