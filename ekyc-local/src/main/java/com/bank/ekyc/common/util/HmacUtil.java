package com.bank.ekyc.common.util;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class HmacUtil {

    public String sign(
            String plainText,
            String secretKey) {

        try {

            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            secretKey.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256");

            mac.init(secretKeySpec);

            byte[] hash =
                    mac.doFinal(
                            plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }
    }
}