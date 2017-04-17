package com.neil.fpdatabase;

import com.neil.fpdatabase.controller.FingerPrintRecognitionResultHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by nhu on 4/6/2017.
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSocket
@ComponentScan(basePackages = "com.neil")
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/finger").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new FingerPrintRecognitionResultHandler();
    }

    public static void main(String[] args){
        SpringApplication.run(WebSocketConfig.class,args);
    }
}