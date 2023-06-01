package com.afl.restaurante.ws.interceptors;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;

// import com.afl.apirest.models.services.UploadFileServiceImpl;

public class RmeSessionChanneInterceptor implements ChannelInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(RmeSessionChanneInterceptor.class);
	
    // @Autowired
    // private JwtDecoder jwtDecoder;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		


		System.out.println("preSend Channel Interceptor");

		// MessageHeaders headers = message.getHeaders();
		// StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//		MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS,
//				MultiValueMap.class);
		
//		for (Map.Entry<String, List<String>> head : multiValueMap.entrySet()) {
//		System.out.println(head.getKey() + "#" + head.getValue());
//	    } 

		//System.out.println(headers);



	    
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			List<String> authorization = accessor.getNativeHeader("X-Authorization");
			//logger.debug("X-Authorization: {}", authorization);

            // String accessToken = authorization.get(0).split(" ")[1];
            // Jwt jwt = jwtDecoder.decode(accessToken);
            // JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            // Authentication authentication = converter.convert(jwt);
            // accessor.setUser(authentication);
		}
		
		return message;
	}

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		// TODO Auto-generated method stub
		System.out.println("postSend Channel Interceptor");

		MessageHeaders headers = message.getHeaders();
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//		MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS,
//				MultiValueMap.class);

		System.out.println(headers);
	}

	@Override
	public boolean preReceive(MessageChannel channel) {
		// TODO Auto-generated method stub

		System.out.println("preReceive Channel Interceptor");

		return false;
	}

	@Override
	public Message<?> postReceive(Message<?> message, MessageChannel channel) {
		// TODO Auto-generated method stub
		System.out.println("postReceive Channel Interceptor");

		MessageHeaders headers = message.getHeaders();
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

//		MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS,
//				MultiValueMap.class);

		System.out.println(headers);
		return null;
	}

}