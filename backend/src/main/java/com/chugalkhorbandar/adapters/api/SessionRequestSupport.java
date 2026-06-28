package com.chugalkhorbandar.adapters.api;

import com.chugalkhorbandar.application.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public final class SessionRequestSupport {

    private SessionRequestSupport() {}

    public static String resolveSessionId(HttpServletRequest request) {
        String header = request.getHeader(SessionConstants.SESSION_HEADER);
        if (header != null && !header.isBlank()) {
            return header.trim();
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (SessionConstants.SESSION_COOKIE.equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
