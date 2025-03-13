package com.prod.realtimechat.shared.jwt;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Slf4j
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private final String ipAddress;  // IP 주소 정보 추가
    private final String bojName;
    private TokenClaims claims;

    // 인증 전
    public JwtAuthenticationToken(String token, String ipAddress, String bojName) {
        super(null);
        this.token = token;
        this.ipAddress = ipAddress;
        this.bojName = bojName;
        setAuthenticated(false);
    }

    // 인증 후
    public JwtAuthenticationToken(TokenClaims claims, String ipAddress, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);

        log.info(claims.getBojName());

        this.token = null;
        this.ipAddress = ipAddress;
        this.bojName = claims.getBojName();
        this.claims = claims;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return claims;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
