package com.java.main.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.print.attribute.standard.Media;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.java.main.externalServices.Role;
import com.java.main.externalServices.User;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AuthSecurityConfig {
	
	
	//get issuer from docker-compose.yml
	@Value("${spring.security.oauth2.authorizationserver.issuer}")
	private String issuerUri;
	
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 1. Authorization server security filter chain (handle /oauth/** endpoints)

	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.oidc(Customizer.withDefaults());  // enable OpenID Connect, profile, OIDC, etc.
		
		http.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.exceptionHandling(exception -> exception.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
			.oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
		
		return http.build();
	}
	
	//---------------------------------------------------------------------------------------------
	
	//	2. Default Security filter chain (handle /login and other endpoints
	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		
		HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
		requestCache.setMatchingRequestParameterName("continue");  // Standard parameter
		
		http.csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.requestCache(cache -> cache.requestCache(requestCache))
			.authorizeHttpRequests(auth -> auth.requestMatchers("/login", "/api/v4/auth/signup", "/error", "/actuator/**", "/favicon.ico", "/.well-known/**")
												.permitAll()
												.requestMatchers(HttpMethod.POST, "/api/v4/auth/admin/create-user").hasRole("ADMIN")
												.requestMatchers("/api/v4/me").authenticated()
												.anyRequest().authenticated()
									)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt-> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
			.formLogin(Customizer.withDefaults());
		
		return http.build();
	}
	
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	    // Tell Spring to look at your custom "roles" claim instead of "scope"
	    grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
	    // Since your DB already has "ROLE_", set prefix to empty string
	    grantedAuthoritiesConverter.setAuthorityPrefix(""); 

	    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
	    return jwtAuthenticationConverter;
	}
	//-------------------------------------------------------------------------------------------------
	
	// 3. Client Registration (React Frontend)
	@Bean
	public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
		RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString()).clientId("my-gateway-client")
												.clientSecret(passwordEncoder.encode("secret"))
												.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
												.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
												.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
												.redirectUri("http://localhost:8084/login/oauth2/code/my-gateway-client")
												.postLogoutRedirectUri("http://localhost:5173/").scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE).scope(OidcScopes.EMAIL)
												.scope("api.read").tokenSettings(tokenSettings()).build();
		
		return new InMemoryRegisteredClientRepository(client);
	}
	
	//Increases time for token to live
	@Bean
	public TokenSettings tokenSettings() {
	    return TokenSettings.builder()
	            .accessTokenTimeToLive(Duration.ofMinutes(60)) // Token valid for 1 hour
	            .refreshTokenTimeToLive(Duration.ofDays(1))    // Refresh token valid for 1 day
	            .reuseRefreshTokens(true)
	            .build();
	}
	
	//------ handling cors policy prefight in Browser ---------------------------- 
	@Bean
	public CorsConfigurationSource configurationSource() {
		CorsConfiguration cors = new CorsConfiguration();
		cors.setAllowedOrigins(List.of("http://localhost:5173/"));
		cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		cors.setAllowedHeaders(List.of("*"));
		cors.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", cors);
		return source;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
		return builder.getAuthenticationManager();
	}
	
	// 4.Authorization server setting (auth server endpoint, when dockering then get issuer url dynamically) 
	@Bean 
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().issuer(issuerUri).build();
	}
	
	// 5. get jwt claim for token and user info
	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> auth2TokenCustomizer(){
		return (context) ->{
			
			//handling access token
			if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())){
				
				context.getClaims().audience(List.of("my-gateway-client"));
				context.getClaims().issuer(issuerUri);  // if working on local machine then '"http://localhost:8086"'
				
				//get authentication wrapper
				Authentication auth = context.getPrincipal();
				
				Set<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
				
				Object principal = auth.getPrincipal();
				
				if(principal instanceof com.java.main.externalServices.User user) {
					
					List<String> dbRole = user.getRoles().stream().map(r ->{
						
						
						
						//System.out.println("role: "+ r.getName());
						return r.getName();
						
					}).toList();// ROLE_ADMIN
					
					authorities.addAll(dbRole);
					
					System.out.println("userId: "+user.getUserId());
					dbRole.forEach(r -> System.out.println("Role_Name : "+r));
					if (user.getUserId() != null) context.getClaims().claim("user_id", user.getUserId());
				    if (user.getName() != null) context.getClaims().claim("name", user.getName());
				    if (user.getEmail() != null) context.getClaims().claim("email", user.getEmail());
				    if (user.getAddress() != null) context.getClaims().claim("address", user.getAddress());
				    if (user.getContactNo() != null) context.getClaims().claim("contactNo", user.getContactNo());
				    if (user.getImageName() != null) context.getClaims().claim("imageName", user.getImageName());
				
					
				}
				
				context.getClaims().claim("roles", authorities);
				}
		};
	}
	//------------------------------------------------------------------------------------------------
	
	// 6. Set jwk source for security context
	@Bean
	public JWKSource<SecurityContext> jwkSource(){
		
		KeyPair keyPair = generateRSAKey(); // custom method for generate rsa key
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	// 6.1 custom method for generate rsa key
	private KeyPair generateRSAKey() {
		KeyPair keyPair;
		
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return keyPair;
	}
	
}
