package com.shego.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

public final class WebUtil {
    public static final String SESSION_USER = "currentUser";
    private static final String SESSION_CSRF_TOKEN = "csrfToken";

    private WebUtil() {
    }

    public static String ensureCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object token = session.getAttribute(SESSION_CSRF_TOKEN);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(SESSION_CSRF_TOKEN, token);
        }
        return token.toString();
    }

    public static boolean validateCsrf(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Object token = session.getAttribute(SESSION_CSRF_TOKEN);
        String formToken = request.getParameter("csrfToken");
        return token != null && token.equals(formToken);
    }

    public static int parsePositiveInt(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
