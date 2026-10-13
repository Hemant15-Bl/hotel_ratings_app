package com.java.main.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.java.main.payload.ChatRequest;
import com.java.main.tool.QuickAnswerService;

@RestController
@RequestMapping("/chat")
public class ChatController {

	private final ChatClient chatClient;
	private final QuickAnswerService quick;

	ChatController(ChatClient chatClient, QuickAnswerService quick) {
		this.chatClient = chatClient;
		this.quick = quick;
	}

	@PostMapping
	  String chat(@RequestBody ChatRequest req,  @AuthenticationPrincipal Jwt jwt) {
		  if (jwt == null || jwt.getSubject() == null) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
			}
			if (req.message() == null || req.message().isBlank()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message is empty");
			}

			String conversationId = jwt.getSubject();   // one conversation per logged-in user

			return quick.tryAnswer(req.message()).orElseGet(() -> {
				@Nullable
				String answer = chatClient.prompt()
								.user(req.message())
								.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
								.call().content();
				return looksLikeToolCall(answer)
						? "I couldn't work that out. Try asking about a hotel's rating, or what guests say about food, staff or rooms."
				        : answer;
			});
	  }
	
	private static boolean looksLikeToolCall(String s) {
	    if (s == null) return false;
	    String t = s.trim();
	    return t.startsWith("{") && t.contains("\"name\"")
	            && (t.contains("\"parameters\"") || t.contains("\"arguments\""));
	}
}
