package link.signalapp.security;

import jakarta.servlet.http.Cookie;
import link.signalapp.model.User;
import link.signalapp.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class TokenRequestFilter extends OncePerRequestFilter {

    private final UserTokenService userTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            tryToAuthorizeUser(request);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        chain.doFilter(request, response);
    }

    private void tryToAuthorizeUser(HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        String requestToken = Arrays.stream(cookies)
                .filter(cookie -> "JAVASESSIONID".equals(cookie.getName())).findAny()
                .map(Cookie::getValue).orElse(null);
        if (requestToken == null) {
            return;
        }
        User user = userTokenService.getUserByToken(requestToken);
        if (user == null) {
            return;
        }
        UserDetails userDetails = new SignalAppUserDetails(user).setToken(requestToken);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext()
                .setAuthentication(usernamePasswordAuthenticationToken);
    }

}
