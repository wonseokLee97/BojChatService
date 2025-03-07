package com.dev.realtimechat.shared.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey key;
    private final String serverIdentifier;
    private final long expirationSeconds;
    private final JwtParser jwtParser;

    public JwtProvider(
            @Value("${token.secret}") String secret,
            @Value("${token.server-ip}") String serverAddress,
            @Value("${token.token-expiration}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.serverIdentifier = generateServerIdentifier(serverAddress, secret);
        this.expirationSeconds = expirationSeconds;
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public String generateToken(String ipAddress, String bojName) {
        Instant now = Instant.now();

        TokenClaims claims = TokenClaims.builder()
                .id(UUID.randomUUID().toString())
                .issuer(serverIdentifier)
                .nameTag(generateNameTag())
                .bojName(bojName)
                .ipAddress(ipAddress)
                .issuedAt(now.getEpochSecond())
                .notBefore(now.minusSeconds(600).getEpochSecond())
                .expiresAt(now.plusSeconds(expirationSeconds).getEpochSecond())
                .build();


        return Jwts.builder()
                .id(claims.id())
                .issuer(claims.issuer())
                .claim("bojName", claims.bojName())
                .claim("regionCode", claims.regionCode())
                .audience().add(claims.ipAddress()).and()
                .subject(claims.nameTag())
                .issuedAt(Date.from(Instant.ofEpochSecond(claims.issuedAt())))
                .notBefore(Date.from(Instant.ofEpochSecond(claims.notBefore())))
                .expiration(Date.from(Instant.ofEpochSecond(claims.expiresAt())))
                .signWith(key)
                .compact();
    }

    public String generateNameTag() {
        // UUID 를 생성하고, 이를 16진수로 변환 후, 대문자로 변경
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        // UUID 에서 필요한 길이만큼 자르고, #을 앞에 붙여서 nameTag 로 생성
        return "#" + uuid.substring(0, 6); // 예: #YQTQJG
    }

    public TokenClaims getTokenClaims(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        return TokenClaims.builder()
                .id(claims.getId())
                .issuer(claims.getIssuer())
                .nameTag(claims.getSubject())
                .bojName(claims.get("bojName", String.class))
                .ipAddress(claims.getAudience().iterator().next())
                .issuedAt(claims.getIssuedAt().getTime() / 1000)
                .notBefore(claims.getNotBefore().getTime() / 1000)
                .expiresAt(claims.getExpiration().getTime() / 1000)
                .build();
    }


    public TokenClaims validateToken(String token, String bojName, String ipAddress) {
        try {
            Claims claims = jwtParser
                    .parseSignedClaims(token)
                    .getPayload();

//            log.info(serverIdentifier);
//            log.info(claims.getIssuer());
//            log.info(claims.getAudience().toString());
//            log.info(claims.get("bojName", String.class) + ", " + bojName);
//            log.info(ipAddress);
            String tokenBojName = claims.get("bojName", String.class);

            // Validate issuer and audience
            if (!claims.getIssuer().equals(serverIdentifier) ||
                    !claims.getAudience().contains(ipAddress)) {
                throw new JwtException.InvalidTokenException("Invalid token claims!!");
            }

            // 현재 로그인한 bojName 와 Token 의 bojName 이 다를 때
            if (!tokenBojName.equals(bojName)) {
                if (bojName == null || bojName.isEmpty()) { // 로그인 정보가 없는 경우
                    throw new JwtException.InvalidBojNameException("Please Login BOJ" + tokenBojName + " != " + bojName);
                } else { // 로그인 정보는 있지만, 토큰 발급시의 로그인 정보와 다른경우
                    throw new BadCredentialsException("Invalid JWT token");
                }
            }

            return TokenClaims.builder()
                    .id(claims.getId())
                    .issuer(claims.getIssuer())
                    .ipAddress(claims.getAudience().iterator().next())
                    .regionCode(claims.getSubject())
                    .issuedAt(claims.getIssuedAt().toInstant().getEpochSecond())
                    .notBefore(claims.getNotBefore().toInstant().getEpochSecond())
                    .expiresAt(claims.getExpiration().toInstant().getEpochSecond())
                    .build();

        } catch (ExpiredJwtException e) {
            throw e;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException |
                 IllegalArgumentException e) {
            throw new JwtException.InvalidTokenException("Invalid JWT token", e);
        }
    }

    private String generateServerIdentifier(String serverAddress, String secret) {
        return UUID.nameUUIDFromBytes(
                (serverAddress + secret).getBytes(StandardCharsets.UTF_8)
        ).toString();
    }
}
