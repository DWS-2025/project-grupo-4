package com.casino.grupo4_dws.casinoweb.managers;

import com.casino.grupo4_dws.casinoweb.security.CSRFTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class CSRFManager {

    private static final String CSRF_TOKEN_ATTR = "csrfToken";
    private static final long TOKEN_EXPIRATION_TIME = 3600000;

    public static void setCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String csrfToken = CSRFTokenUtil.generateToken();
        session.setAttribute(CSRF_TOKEN_ATTR, csrfToken);
    }

    public static String getCSRFToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(CSRF_TOKEN_ATTR);
    }

    public static void regenerateCSRFToken(HttpServletRequest request) {
        setCSRFToken(request);
    }
}
