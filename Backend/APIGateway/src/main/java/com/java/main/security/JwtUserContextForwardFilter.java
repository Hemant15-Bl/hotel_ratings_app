package com.java.main.security;

import java.util.stream.Collectors;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.stereotype.Component;


import reactor.core.publisher.Mono;

@Component
public class JwtUserContextForwardFilter implements GlobalFilter, Ordered{

	@Override
	public int getOrder() {
		// default security filter chain order from authserver
		return 2;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		return ReactiveSecurityContextHolder.getContext()
		        // 1. Get the Authentication object from the SecurityContext
		        .map(SecurityContext::getAuthentication)
		        
		        // 2. Filter, ensuring the Authentication object is not null AND is authenticated
		        .filter(auth -> auth != null && auth.isAuthenticated()) 
		        
		        // 3. Now that we have the valid, authenticated 'auth' object, we can proceed
		        .flatMap(auth -> {
		            
		            // The rest of your logic (extracting the JWT and modifying the header) goes here.
		            
		            if (auth.getPrincipal() instanceof Jwt jwt) {
		                // ... (extract claims and build modified request) ...
		                
		                // Example of extraction:
		                String userId = jwt.getSubject(); 
		                String tokenValue = jwt.getTokenValue();
		                
		                String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
		                
		                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
		                    .header("X-Auth-User-Id", userId)
		                    .header("X-Auth-User-Roles", authorities)
		                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+tokenValue)
		                    .build();
		                
		                return chain.filter(exchange.mutate().request(modifiedRequest).build());
		            }
		            return chain.filter(exchange);
		        })
		        // 4. Handle cases where the context is empty or unauthenticated (e.g., public endpoints)
		        .switchIfEmpty(chain.filter(exchange));
	}
	

}
