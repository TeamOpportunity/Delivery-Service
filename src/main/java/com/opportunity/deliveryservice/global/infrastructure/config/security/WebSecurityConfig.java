package com.opportunity.deliveryservice.global.infrastructure.config.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.opportunity.deliveryservice.global.infrastructure.jwt.JwtUtil;
import com.opportunity.deliveryservice.global.infrastructure.redis.RedisService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // @Secured(권한 별 접근 제어) 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final RedisService redisService;

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, redisService);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		return filter;
	}

	@Bean
	public JwtAuthorizationFilter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisService);
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		// 권한 계층 구조 설정
		return RoleHierarchyImpl.fromHierarchy("""
			ROLE_MASTER > ROLE_MANAGER
			ROLE_MANAGER > ROLE_OWNER
			ROLE_MANAGER > ROLE_CUSTOMER
			""");

	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// CSRF 설정
		http.csrf((csrf) -> csrf.disable());

		// 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
		http.sessionManagement((sessionManagement) ->
			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests((authorizeHttpRequests) ->
			authorizeHttpRequests
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
				.permitAll() // resources 접근 허용 설정
				.requestMatchers("/")
				.permitAll() // 메인 페이지 요청 허가
				.requestMatchers("/v1/users/signup", "/v1/users/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/v1/stores/*/reviews").permitAll()
				.requestMatchers(HttpMethod.GET, "/v1/reviews/*").permitAll()
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
				// 권한이 필요 없는 요청 접근 허가
				// "/v1/users/sighup","/v1/users/login"로 시작하는 요청 모두 접근 허가
				.anyRequest()
				.authenticated() // 그 외 모든 요청 인증처리
		);

		http.formLogin((formLogin) ->
			formLogin
				.disable()
		);

		// http.formLogin((formLogin) ->
		// 	formLogin
		// 		.loginPage("/").permitAll()
		// );

		// 필터 관리(앞에있는 필터 먼저 실행)
		http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
