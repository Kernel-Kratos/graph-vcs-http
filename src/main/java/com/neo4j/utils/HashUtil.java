package com.neo4j.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;


public class HashUtil {
    public static String hashString(String base) { //this is thread safe as i'm doing local instantiation
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final String hexString; 
            HexFormat hexFormat = HexFormat.of();
            hexString = hexFormat.formatHex(hash); 
            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashBlob (byte[] blob) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(blob);
        byte[] hash = digest.digest();
        final String hexString;
        HexFormat hexFormat = HexFormat.of();
        hexString = hexFormat.formatHex(hash);
        return hexString.toString();
    }

}
