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

import com.bside.someday.oauth.handler.CustomOAuth2SuccessHandler;
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

			.csrf().disable()
			.httpBasic().disable()
			.formLogin().disable()

			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.authorizeRequests()
			.antMatchers("/api/**").authenticated()
			.anyRequest().permitAll()

			// TODO. Exception 핸들링 추가
			// .exceptionHandling()
			// .authenticationEntryPoint(jwtAuthenticationEntryPoint)
			// .accessDeniedHandler(jwtAccessDeniedHandler)

			.and()
			.oauth2Login()
			.userInfoEndpoint()
			.userService(customOAuth2UserService)

			.and()
			.successHandler(customOAuth2SuccessHandler)

			.and().build();
	}
}
