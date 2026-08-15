package com.scrumify.scrumify.api.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.scrumify.scrumify.application.common.interfaces.CurrentUserService;
import com.scrumify.scrumify.application.common.interfaces.JwtService;
import com.scrumify.scrumify.application.common.model.JwtClaim;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CurrentUserService currentUserService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CurrentUserService currentUserService) {
        this.jwtService = jwtService;
        this.currentUserService = currentUserService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                JwtClaim claim = jwtService.verifyAccessToken(token);
                currentUserService.setCurrentUser(claim);

                var authority = new SimpleGrantedAuthority("ROLE_" + claim.roles());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        claim.id(),
                        null,
                        List.of(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("scrumify_access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
