package com.example.signalapp.endpoint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class EndpointBase {

    protected static final String JAVASESSIONID = "JAVASESSIONID";

    protected void setCookieWithTokenToResponse(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(JAVASESSIONID, token);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
