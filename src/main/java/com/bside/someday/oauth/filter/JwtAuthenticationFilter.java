package com.bside.someday.oauth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bside.someday.oauth.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String BEARER_TOKEN_PREFIX = "Bearer ";
	public static final int BEARER_TOKEN_PREFIX_LENGTH = 7;

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 토큰 헤더로 받음
		String token = resolveToken(request);

		// 토큰 쿠키로 받음
		// String token = jwtTokenProvider.getJwtToken(request);

		//TODO: JWT 파싱 exception handling 구현 필요

		if (jwtTokenProvider.validate(token)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(token) && token.startsWith(BEARER_TOKEN_PREFIX)) {
			return token.substring(BEARER_TOKEN_PREFIX_LENGTH);
		}
		return null;
	}

}
