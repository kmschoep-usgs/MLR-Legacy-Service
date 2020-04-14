package gov.usgs.wma.mlrlegacy.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.info.Info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@OpenAPIDefinition(info = @Info(title = "MLR API", version = "v1"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${oauthResourceJwkSetUri:}")
	private String jwkSetUri;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable().cors()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().csrf().disable()
			.authorizeRequests()
				.antMatchers("/swagger-resources/**", "/webjars/**", "/v3/**", "/public").permitAll()
				.antMatchers("/version", "/info**", "/health/**", "/favicon.ico", "/swagger-ui/**").permitAll()
				.antMatchers("/actuator/health**").permitAll()
				.anyRequest().authenticated()
			.and().oauth2ResourceServer().authenticationEntryPoint(standardAuthEntryPoint()).jwt(
				jwt -> jwt.jwtAuthenticationConverter(keyCloakJWTConverter())
			)
		;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry){
				registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods("GET","PUT","POST","DELETE","PATCH");
			}
		};
	}

	@Bean
	public AuthenticationEntryPoint standardAuthEntryPoint() {
		return new AuthenticationEntryPoint(){
		
			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {
					final Map<String, Object> mapBodyException = new HashMap<>() ;

					mapBodyException.put("error_message", authException.getMessage()) ;
					response.setContentType("application/json") ;
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED) ;
					new ObjectMapper().writeValue(response.getOutputStream(), mapBodyException) ;
			}
		};
	}

	private Converter<Jwt, AbstractAuthenticationToken> keyCloakJWTConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter =
				new JwtAuthenticationConverter();
	
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
				(new KeycloakJWTAuthorityMapper());
			
		return jwtAuthenticationConverter;
	}
}