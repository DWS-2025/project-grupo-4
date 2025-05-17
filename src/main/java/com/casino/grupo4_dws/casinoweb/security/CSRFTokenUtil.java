package com.casino.grupo4_dws.casinoweb.security;

import java.security.SecureRandom;
import java.util.Base64;

public class CSRFTokenUtil {

    public static String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}