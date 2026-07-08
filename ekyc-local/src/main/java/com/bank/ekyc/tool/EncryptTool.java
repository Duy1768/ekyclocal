package com.bank.ekyc.tool;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptTool {

    public static void main(String[] args) {

        String masterKey = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");

        if (masterKey == null || masterKey.isBlank()) {
            throw new RuntimeException("JASYPT_ENCRYPTOR_PASSWORD not found");
        }

        if (args.length != 1) {
            throw new RuntimeException("Usage: EncryptTool <password>");
        }

        StandardPBEStringEncryptor encryptor =
                new StandardPBEStringEncryptor();

        encryptor.setPassword(masterKey);

        String encrypted = encryptor.encrypt(args[0]);

        System.out.println("Encrypted:");
        System.out.println(encrypted);
    }
}