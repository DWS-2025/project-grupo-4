package com.casino.grupo4_dws.casinoweb.security;

import jakarta.servlet.http.HttpServletRequest;

import static org.apache.tomcat.util.http.RequestUtil.isValidOrigin;

public class CSRFValidator {

    public static boolean validateCSRFToken(HttpServletRequest request) {
        String sessionToken = CSRFService.getCSRFToken(request);
        String requestToken = request.getParameter("csrfToken");
        String origin = request.getHeader("Origin");

        if (origin != null && !isValidOrigin(origin)) {
            return false;
        }
        return sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
    }
}
