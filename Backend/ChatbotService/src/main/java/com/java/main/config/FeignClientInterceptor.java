package com.java.main.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;

@Component
public class FeignClientInterceptor {

	private static final Logger log = LoggerFactory.getLogger(FeignClientInterceptor.class);

	@Bean
	public RequestInterceptor resInterceptor() {
	    return template -> {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        
	        if (authentication != null && authentication.getCredentials() instanceof Jwt jwt) {
	            String token = jwt.getTokenValue();
	            template.header("Authorization", "Bearer " + token);
	            log.debug("Token forwarded successfully from SecurityContext!");
	        } else {
	        	log.debug("Fallback triggered or SecurityContext empty - No token found.");
	        }
	    };
	}
}
