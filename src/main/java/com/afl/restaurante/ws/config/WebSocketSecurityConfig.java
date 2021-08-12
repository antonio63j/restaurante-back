package com.afl.restaurante.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {



    @Override
    protected boolean sameOriginDisabled() {
    	System.out.println("invocado");
        return true;
    }
    
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                //.nullDestMatcher().authenticated() 
                .simpSubscribeDestMatchers("/topic/datosEmpresa").permitAll() 

                .simpSubscribeDestMatchers("/user/**", "/topic/friends/*").hasRole("USER");
              //  .anyMessage().denyAll();

    }
    
    
}