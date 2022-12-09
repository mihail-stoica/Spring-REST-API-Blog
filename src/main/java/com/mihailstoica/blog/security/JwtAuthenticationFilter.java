package com.mihailstoica.blog.security;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // get JWT token from http request
        String jwtToken = getJwtTokenFromRequest(request);

        // validate token
        if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
            // get username from token
            String username = tokenProvider.getUsernameFromJwt(jwtToken);
        }


        // load user associated with the token

        // set to spring security


    }

    // Bearer <accessToken>
    private String getJwtTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        String jwtToken = null;

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            jwtToken = bearerToken.substring(7, bearerToken.length());
        }

        return jwtToken;
    }
}
