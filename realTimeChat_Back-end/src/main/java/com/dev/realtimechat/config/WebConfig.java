//package com.dev.realtimechat.config;
//
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/connect/**")  // 모든 경로에 대해
//                .allowedOrigins(
//                        "https://d520-2406-5900-1145-6421-b84d-1190-282d-68d2.ngrok-free.app",
//                        "https://e5d9-2406-5900-1145-6421-b84d-1190-282d-68d2.ngrok-free.app")  // 모든 출처 허용
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메서드
//                .allowCredentials(true);
//    }
//}
