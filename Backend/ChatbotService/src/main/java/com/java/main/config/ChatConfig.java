package com.java.main.config;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.java.main.tool.HotelTools;

@Configuration
public class ChatConfig {

	 @Bean
	  ChatMemory chatMemory() {
	    return MessageWindowChatMemory.builder().maxMessages(4).build();
	  }

	
	 
	  @Bean
	  ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore, ChatMemory chatMemory, HotelTools hotelTools) {
	    return builder
	      .defaultSystem("""
	        Hotel review assistant. Ratings are out of 10.
		    Answer only from the provided review context, in 2 or 3 short sentences.
		    Name hotels, never IDs. If the context doesn't answer it, say you don't know.
	        """)
	      .defaultTools(hotelTools)
	      .defaultAdvisors(
	        MessageChatMemoryAdvisor.builder(chatMemory).build(),
	        QuestionAnswerAdvisor.builder(vectorStore)
	          .searchRequest(SearchRequest.builder().topK(3).filterExpression("type == 'review'").build()).build(),
	          new SimpleLoggerAdvisor()
	    		  )
	      .build();
	  }
}
