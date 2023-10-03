package link.signalapp.endpoint;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class EndpointBase {

    protected static final String JAVASESSIONID = "JAVASESSIONID";

    protected void setCookieWithTokenToResponse(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(JAVASESSIONID, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

}
