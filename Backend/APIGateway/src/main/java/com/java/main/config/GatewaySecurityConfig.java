package com.java.main.config;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {
	
	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

	//------------ providing endpoints and authenticate them ------------------------------------------------------------
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveOAuth2AuthorizedClientService clientService) {
		http.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.cors(Customizer.withDefaults())
			.requestCache(cache -> cache.requestCache(NoOpServerRequestCache.getInstance())) // clear cache after logout
			.authorizeExchange(exchange -> exchange.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
													.pathMatchers("/oauth2/**", "/login/**", "/actuator/**", "/api/v4/auth/signup", "/favicon.ico","/.well-known/appspecific/**").permitAll()
													.pathMatchers("/api/**").authenticated()
													.anyExchange().authenticated()
								)
			.oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(redirectToReactSuccessHandler(clientService)))
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder())))
			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.logout(logout -> logout.requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
									.logoutHandler((exchange, authentication) -> {
						                return exchange.getExchange().getSession().flatMap(session -> session.invalidate());
						                })
									.logoutSuccessHandler(oidcLogoutSuccessHandler()))
			.exceptionHandling(ex -> ex
//            		.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)
            		 .authenticationEntryPoint((exchange, e) -> {
            		        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            		        return exchange.getResponse().setComplete();
            	        }
                    )
                );

	return http.build();
	}
	
	//-------------- handling method for redirect to react app -------------------------------
	@Bean
	public ServerAuthenticationSuccessHandler redirectToReactSuccessHandler(ReactiveOAuth2AuthorizedClientService clientService) {
		return (exchange, authentication) ->{
			OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
			
			return clientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName())
								.flatMap(client -> {
									
									String accessToken = client.getAccessToken().getTokenValue(); // get token
									String redirectURL = "http://localhost:5173/oauth2/callback#access_token="+accessToken; // create url to callback react with token  use fragment(#) while production instead of query (?)
									
									// send response to react
									ServerHttpResponse response = exchange.getExchange().getResponse();
									response.setStatusCode(HttpStatus.FOUND);
									response.getHeaders().setLocation(URI.create(redirectURL));
									return response.setComplete();
								});
		};
	}
	// ---------------------------------------------------------------------------------------------------------
	
	//----------- fecthing the public key ---------------------------------------------
	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		// This will fetch the public keys from http://localhost:8084/.well-known/openid-configuration
		//if running on local then issuer-uri '"http://localhost:8086"'
		return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
	
	}
	// ---------------------------------------------------------------------------------------------------------

	//---------------- Logout and clear session cookies (logout + oidc + connect/logout) ----------------------
	@Bean
	public ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
		return (exchange, authentication) ->{
			
			ServerHttpResponse response = exchange.getExchange().getResponse();
			response.addCookie(ResponseCookie.from("JSESSIONID", "").path("/").maxAge(0).build()); // clearing session cookies
			response.addCookie(ResponseCookie.from("SESSION", "").path("/").maxAge(0).build());
			
			if(authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
				String idToken = oidcUser.getIdToken().getTokenValue();
				String logoutUri = "http://localhost:8086/connect/logout"+
								"?id_token_hint="+ idToken +
								"&post_logout_redirect_uri=http://localhost:5173/";
				response.setStatusCode(HttpStatus.FOUND);
				response.getHeaders().setLocation(URI.create(logoutUri));
				
			}else {
				response.setStatusCode(HttpStatus.FOUND);
				response.getHeaders().setLocation(URI.create("http://localhost:5173/"));
			}
			
			return response.setComplete();
		};
	}
	//-------------------------------------------------------------------------------------
	
	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of("http://localhost:5173"));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
	    config.setAllowCredentials(false); // JWT header auth

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}
}
