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
	public static final String ACCESS_TOKEN_PREFIX = "access ";
	public static final int ACCESS_TOKEN_PREFIX_LENGTH = 7;

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		//TODO: path 별 처리
		String path = request.getServletPath();

		//FIXME: 토큰 전송 방법

		// 토큰은 헤더로 전송
		String token = resolveToken(request);
		// 쿠키에서..?
		// String token = jwtTokenProvider.getJwtToken(request);

		//TODO: JWT 파싱 exception handling 구현 필요

		if (StringUtils.hasText(token) && jwtTokenProvider.validate(token)) {
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(token) && token.startsWith(ACCESS_TOKEN_PREFIX)) {
			return token.substring(ACCESS_TOKEN_PREFIX_LENGTH);
		}
		return null;
	}
}
