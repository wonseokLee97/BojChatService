package com.dev.realtimechat.chat.config;

import com.dev.realtimechat.shared.jwt.JwtChannelInterceptor;
import com.dev.realtimechat.shared.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("접속 시도 감지");

        // 주소: ws://localhost:8080/chat
        // sockJS 클라이언트가 WebSocket HandShake를 하기 위해 연결할 endPoint를 지정
        registry.addEndpoint("/connect")
//                .setAllowedOriginPatterns(
//                        "http://127.0.0.1:5500",
//                        "chrome-extension://ajiigcomfhhjookhjjlilajjidbfhddl",
//                        "https://www.acmicpc.net"
//                )
                .setAllowedOriginPatterns(
                        "http://127.0.0.1:5500",
                        "https://www.acmicpc.net"
                )
                .withSockJS() // 웹 소켓을 지원하지 않는 브라우저는 sockJS를 사용
        ;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // MessageBroker가 해당 "/sub" api를 구독하고 있는 클라이언트에게 메시지를 전달한다.
        // heart.beat 설정 25000ms -> 25s
        registry.enableSimpleBroker("/sub", "/queue")
                .setTaskScheduler(taskScheduler())
                .setHeartbeatValue(new long[] {25000, 25000});

        // 클라이언트로 부터 메시지를 받을 api의 prefix를 설정한다. "/pub/~~"
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // JwtChannelInterceptor 실행
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtProvider));
    }

    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        return taskScheduler;
    }
}
