package com.prod.realtimechat.shared.jwt;

import com.prod.realtimechat.shared.global.api.ApiResponse;
import com.prod.realtimechat.shared.global.type.http.HttpErrorType;
import com.prod.realtimechat.shared.global.type.http.HttpSuccessType;
import com.prod.realtimechat.shared.utils.IpAddressUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;


@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Set<String> SECURED_API_PATHS = Set.of(
            "/init", "/chatroom/**", "/message/**"
    );

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * HTTP 요청을 필터링하여 JWT 토큰을 확인하고, 토큰이 유효하면 인증을 수행
     * 토큰이 없거나 만료된 경우 새로운 토큰을 발급
     *
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveJwtToken(request);
        String ipAddress = IpAddressUtil.extractIpAddress(request); // Client IP
        String bojName =  resolveBojByJwt(request);

        // Case the token doesn't exist
        if (token == null || token.equals("null")) {
            handleNoToken(ipAddress, bojName, response);
            return;
        }

        //Case the token exists
        try {
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token, ipAddress, bojName);
            // jwtAuthenticationProvider 로 위임
            Authentication authentication = authenticationManager.authenticate(authRequest);
            log.info("Authentication 설정됨: {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info(request.toString());
            log.info(response.toString());

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handleExpiredToken(response, ipAddress, bojName);
        } catch (Exception e) {
            handleJwtException(response, e);
        }
    }


    /**
     * 발생한 예외에 따라 적절한 에러 메시지를 반환하는 메서드
     *
     * @param response HTTP 응답 객체
     * @param e 발생한 예외
     * @throws IOException 입출력 예외
     */
    private void handleJwtException(HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof BadCredentialsException) {
            handleInvalidToken(response, "INVALID_CREDENTIALS"); // 자격 증명이 잘못된 경우
        } else if (e instanceof SecurityException) {
            handleInvalidToken(response, "SECURITY_ERROR"); // 보안 관련 문제
        } else if (e instanceof MalformedJwtException) {
            handleInvalidToken(response, "MALFORMED_JWT"); // JWT 형식 오류
        } else if (e instanceof UnsupportedJwtException) {
            handleInvalidToken(response, "UNSUPPORTED_JWT"); // JWT 지원되지 않음
        } else if (e instanceof IllegalArgumentException) {
            handleInvalidToken(response, "INVALID_ARGUMENT"); // 잘못된 인자
        } else {
            // 기본적으로 처리되지 않은 예외
            log.error("Unexpected error: {}", e.getMessage());
        }
    }

    /**
     * 토큰이 존재하지 않을 경우 새 토큰을 발급하여 응답하는 메서드
     *
     * @param ipAddress 클라이언트 IP 주소
     * @param bojName BOJ 아이디
     * @param response HTTP 응답 객체
     * @throws IOException 입출력 예외
     */
    private void handleNoToken(String ipAddress, String bojName, HttpServletResponse response) throws IOException {
        String newToken = jwtProvider.generateToken(ipAddress, bojName);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.success(
                        newToken,
                        HttpSuccessType.SUCCESS_TOKEN_ISSUANCE)
        );
    }

    /**
     * 만료된 토큰에 대해 새 토큰을 발급하여 응답하는 메서드
     */
    private void handleExpiredToken(HttpServletResponse response, String ipAddress, String bojName) throws IOException {
        String newToken = jwtProvider.generateToken(ipAddress, bojName);
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(
                newToken,
                "EXPIRED_TOKEN",
                HttpErrorType.EXPIRED_TOKEN));
    }

    /**
     * JWT 토큰이 유효하지 않은 경우 에러 메시지를 반환하는 메서드
     */
    private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(
                message,
                HttpErrorType.INVALID_TOKEN));
    }

    /**
     * 새로운 토큰을 발급하여 응답하는 메서드
     */
    private void sendTokenResponse(HttpServletResponse response, String token) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.success(
                token,
                HttpSuccessType.SUCCESS_TOKEN_ISSUANCE));
    }


    /**
     * HTTP 요청에서 JWT 토큰을 추출하는 메서드
     */
    private String resolveJwtToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JwtProperties.HEADER_STRING);
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            return authorizationHeader.substring(JwtProperties.TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * HTTP 요청에서 BOJ 아이디를 추출하는 메서드
     */
    private String resolveBojByJwt(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("bojName");
        if (StringUtils.hasText(authorizationHeader)) {
            return authorizationHeader;
        }

        return null;
    }


    /**
     * SECURED_API_PATHS 에 정의된 경로를 제외한 모든 요청에 대해 필터를 적용하지 않도록 하는 메서드
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return SECURED_API_PATHS.stream()
                .noneMatch(pattern -> antPathMatcher.match(pattern, path));
    }
}