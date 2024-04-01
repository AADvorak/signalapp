package link.signalapp.security;

import jakarta.servlet.http.Cookie;
import link.signalapp.model.User;
import link.signalapp.service.UserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> "JAVASESSIONID".equals(cookie.getName()))
                    .findAny()
                    .map(Cookie::getValue)
                    .ifPresent(this::authorizeUser);
        }
        chain.doFilter(request, response);
    }

    private void authorizeUser(String token) {
        User user = userTokenService.getUserByToken(token);
        if (user != null) {
            UserDetails userDetails = new SignalAppUserDetails(user).setToken(token);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);
        }
    }
}
