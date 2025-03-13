package com.prod.realtimechat.shared.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtProvider jwtProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            throw new IllegalArgumentException("Unsupported authentication type");
        }

        JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
        String token = (String) jwtAuthentication.getCredentials();
        String ipAddress = jwtAuthentication.getIpAddress();
        String bojName = jwtAuthentication.getBojName();

        log.info("유효성 검사 실시");
        TokenClaims claims = jwtProvider.validateToken(token, bojName, ipAddress);
        log.info("유효성 검사 완료");
        return new JwtAuthenticationToken(claims, ipAddress, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}