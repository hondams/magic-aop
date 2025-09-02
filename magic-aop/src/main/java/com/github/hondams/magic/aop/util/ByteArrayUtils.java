package com.github.hondams.magic.aop.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ByteArrayUtils {

    public byte[] fromHex(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length. len=" + len);
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int first = Character.digit(hex.charAt(i), 16);
            int second = Character.digit(hex.charAt(i + 1), 16);
            if (first == -1 || second == -1) {
                throw new IllegalArgumentException("Invalid hex character in string: " + hex);
            }
            data[i / 2] = (byte) ((first << 4) + second);
        }
        return data;

    }

    public String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
