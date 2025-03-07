package com.dev.realtimechat.shared.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class IpAddressUtil {
    private static final List<String> IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "X-Real-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_VIA",
            "IPV6_ADR"
    );

    public static String extractIpAddress(HttpServletRequest request) throws UnknownHostException {
        String clientIp = null;
        boolean isIpInHeader = false;

        for (String header : IP_HEADERS) {
            clientIp = request.getHeader(header);
            if (StringUtils.hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
                isIpInHeader = true;
                break;
            }
        }

        if (!isIpInHeader) {
            clientIp = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
            InetAddress address = InetAddress.getLocalHost();
            clientIp = address.getHostAddress();
        }

        log.info("IP: {}", clientIp);
        return clientIp;
    }

//    public static String extractIpAddress(HttpServletRequest request) throws UnknownHostException {
//        String clientIp = request.getHeader("X-Forwarded-For");
//        // 앞서 작성한 조건문 또는 헤더 순회 방법을 통해 clientIp 값 설정
//
//        log.info("IP: {}", clientIp);
//
//        if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
//            InetAddress address = InetAddress.getLocalHost();
//            clientIp = address.getHostAddress();
//        }
//
//        return clientIp;
//    }

//    public static String extractIpAddress(HttpServletRequest request) {
//        return IP_HEADERS.stream()
//                .map(request::getHeader)
//                .filter(StringUtils::hasText)
//                .map(ipList -> ipList.split(",")[0].trim())
//                .filter(IpAddressUtil::isValidIpAddress)
//                .findFirst()
//                .orElseGet(request::getRemoteAddr);
//    }

//    private static boolean isValidIpAddress(String ip) {
//        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
//            return false;
//        }
//
//        return ip.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}$") || // IPv4
//                ip.matches("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$"); // IPv6
//    }
}