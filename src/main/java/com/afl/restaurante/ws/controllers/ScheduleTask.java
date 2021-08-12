package com.afl.restaurante.ws.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.text.SimpleDateFormat;
import java.util.Date;

@EnableScheduling
@Controller
public class ScheduleTask {

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/send/message")
	public void onReceivedMesage(String message) {
		this.template.convertAndSend("/chat", new SimpleDateFormat("HH:mm:ss").format(new Date()) + "- " + message);
	}

	@Scheduled(fixedRate = 5000)
	public void trigger() {
		//template.convertAndSend("/topic/message", new SimpleDateFormat("HH:mm:ss").format(new Date()));
		// template.convertAndSend("/topic/datosEmpresa", new Date());

		System.out.println("hola");
	}

}