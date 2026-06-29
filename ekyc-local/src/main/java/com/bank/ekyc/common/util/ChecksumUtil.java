package com.bank.ekyc.common.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

@UtilityClass
public class ChecksumUtil {

    public String sha256(
            byte[] content) {

        return DigestUtils.sha256Hex(
                content);
    }
}