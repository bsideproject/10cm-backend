package com.bside.someday.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bside.someday.config.web.CorsConfig;
import com.bside.someday.oauth.filter.JwtAuthenticationFilter;
import com.bside.someday.oauth.handler.CustomOAuth2SuccessHandler;
import com.bside.someday.oauth.handler.JwtAccessDeniedHandler;
import com.bside.someday.oauth.handler.JwtAuthenticationEntryPoint;
import com.bside.someday.oauth.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;

	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final CorsConfig corsConfig;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web -> web.ignoring()
			.antMatchers("/swagger-ui/**")
			.antMatchers("/favicon*/**")
			.antMatchers("/resources/**"));
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws
		Exception {

		return http

			// h2-console, swagger-ui
			.csrf().disable()
			.headers().frameOptions().disable()

			.and()
			.httpBasic().disable()
			.formLogin().disable()

			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()

			.antMatchers("/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs").permitAll()
			.antMatchers("/api/v1/auth/**").permitAll()
			.antMatchers("/api/v1/resources/**").permitAll()
			.antMatchers(HttpMethod.GET, "/api/v1/trip/**").permitAll()
			.antMatchers("/login/**").permitAll()

			// 설정 값 이외 URL
			.anyRequest().authenticated()

			.and()
			.exceptionHandling()
			.accessDeniedHandler(jwtAccessDeniedHandler)
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)

			// OAuth2 로그인 이후 진입점 설정
			.and()
			.oauth2Login()
			.userInfoEndpoint() // 로그인 성공 이후 사용자 정보 가져올 때 설정
			.userService(customOAuth2UserService)

			.and()
			.successHandler(customOAuth2SuccessHandler)

			.and()
			.addFilter(corsConfig.corsFilter())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
