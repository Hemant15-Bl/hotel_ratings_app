package com.java.main.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.main.payload.ChatRequest;

@RestController
@RequestMapping("/chat")
public class ChatController {

	private final ChatClient chatClient;
	
	  ChatController(ChatClient chatClient) {
		  this.chatClient = chatClient; 
	}

	  @PostMapping
	  String chat(@RequestBody ChatRequest req) {
	    return chatClient.prompt()
	      .user(req.message())
	      .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, req.conversationId()))
	      .call().content();
	  }
}
