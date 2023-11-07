package com.jimdimas.api.config;

import com.jimdimas.api.token.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        System.out.println("filter");
        if (request.getCookies()!=null) {
            Optional<Cookie> accessCookieExists = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("accessToken")).findFirst();
            Optional<Cookie> refreshCookieExists = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refreshToken")).findFirst();
            Boolean accessTokenValidated=false;
            if (accessCookieExists.isPresent()) {
                String accessToken = accessCookieExists.get().getValue();
                if (accessToken != null && !accessToken.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = jwtService.extractSubject(accessToken);
                    if (username!=null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (jwtService.verifyToken(accessToken) && userDetails.isEnabled()) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            request.setAttribute("user", userDetails);
                            accessTokenValidated=true;
                        }
                    }
                }
            }
            if (refreshCookieExists.isPresent() && !accessTokenValidated) {
                String refreshToken = refreshCookieExists.get().getValue();
                if (refreshToken != null && !refreshToken.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = tokenService.verifyByRefreshToken(refreshToken);
                    if (userDetails.isEnabled()) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        request.setAttribute("user", userDetails);
                        Cookie accessCookie = new Cookie("accessToken", jwtService.generateAccessToken(userDetails));
                        accessCookie.setPath("/");
                        response.addCookie(accessCookie);
                    }
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
