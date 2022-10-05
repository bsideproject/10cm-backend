package com.bside.someday.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web -> web.ignoring()
			.antMatchers("/swagger-ui/**")
			.antMatchers("/favicon*/**")
			.antMatchers("/resources/**")
		);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws
		Exception {

		return http

			.csrf().disable()
			.httpBasic().disable()
			.formLogin().disable()

			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			// FIXME: URL 권한 설정
			.and()
			.authorizeRequests()
			.antMatchers("/swagger-resources/**", "/swagger-ui/**", "/v2/api-docs").permitAll()
			.antMatchers("/api/v1/auth/**").permitAll()

			// FIXME: 커밋 X
			.antMatchers("/login").permitAll()

			.anyRequest().authenticated()

			.and()
			.exceptionHandling()
			.accessDeniedHandler(jwtAccessDeniedHandler)
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)

			.and()
			.oauth2Login()
			.userInfoEndpoint()
			.userService(customOAuth2UserService)

			.and()
			.successHandler(customOAuth2SuccessHandler)

			.and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}
}
