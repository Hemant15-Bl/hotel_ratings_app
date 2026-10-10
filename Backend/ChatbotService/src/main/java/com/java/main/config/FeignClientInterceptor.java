package com.java.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;

@Component
public class FeignClientInterceptor {

	@Bean
	public RequestInterceptor resInterceptor() {
	    return template -> {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        
	        if (authentication != null && authentication.getCredentials() instanceof Jwt jwt) {
	            String token = jwt.getTokenValue();
	            template.header("Authorization", "Bearer " + token);
	            System.out.println("Token forwarded successfully from SecurityContext!");
	        } else {
	            System.err.println("Fallback triggered or SecurityContext empty - No token found.");
	        }
	    };
	}
}
