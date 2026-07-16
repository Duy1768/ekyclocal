package com.bank.ekyc.tool;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptTool {

    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    public static void main(String[] args) throws Exception {

        if (args.length != 1) {
            throw new RuntimeException("Usage: EncryptTool <password>");
        }

        String password = args[0];

        String masterKey = System.getenv("AES_MASTER_KEY");

        if (masterKey == null || masterKey.isBlank()) {
            throw new RuntimeException("AES_MASTER_KEY not found");
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(masterKey.getBytes(StandardCharsets.UTF_8));

        SecretKey secretKey =
                new javax.crypto.spec.SecretKeySpec(key, "AES");

        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec =
                new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

        byte[] encrypted =
                cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

        byte[] result = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        System.out.println(Base64.getEncoder().encodeToString(result));
    }
}