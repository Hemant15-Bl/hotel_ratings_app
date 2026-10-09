package com.java.main.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.java.main.tool.HotelTools;

@Configuration
public class ChatConfig {

	 @Bean
	  ChatMemory chatMemory() {
	    return MessageWindowChatMemory.builder().maxMessages(10).build();
	  }

	
	 
	  @Bean
	  ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore, ChatMemory chatMemory, HotelTools hotelTools) {
	    return builder
	      .defaultSystem("""
	        You are an assistant for a hotel rating platform. Ratings are on a scale of 1 to 10.

		    Rules:
		    - For ONE hotel's average rating or review count, call getHotelSummary with the hotel name.
		    - For best/worst hotel, rankings or comparisons, call rankHotelsByAverageRating.
		    - These are the only tools. Never invent other tool names.
		    - Never write JSON or tool names in your answer, and never tell the user to call a tool.
		    - Use the provided review context only for opinions (food, staff, view, rooms).
		    - Always refer to hotels by name.
		    - If you don't know, say so. Never invent hotels, ratings or reviews.
	        """)
	      .defaultTools(hotelTools)
	      .defaultAdvisors(
	        MessageChatMemoryAdvisor.builder(chatMemory).build(),
	        QuestionAnswerAdvisor.builder(vectorStore)
	          .searchRequest(SearchRequest.builder().topK(8).filterExpression("type == 'review'").build()).build(),
	          new SimpleLoggerAdvisor()
	    		  )
	      .build();
	  }
}
