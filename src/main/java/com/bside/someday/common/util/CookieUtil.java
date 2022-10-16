package com.bside.someday.common.util;

import static org.springframework.http.HttpHeaders.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	@Value("${authentication.cookie.domain}")
	private String cookieDomain;
	@Value("${authentication.cookie.accessTokenName}")
	private String accessTokenName;
	@Value("${authentication.cookie.refreshTokenName}")
	private String refreshTokenName;
	@Value("${authentication.jwt.accessTokenExpirationSecond}")
	private Long accessTokenExpirationSecond;
	@Value("${authentication.jwt.refreshTokenExpirationSecond}")
	private Long refreshTokenExpirationSecond;

	public void addAccessTokenResponseCookie(HttpServletResponse response, String token) {
		response.addHeader(SET_COOKIE, createAccessTokenResponseCookie(token).toString());
	}

	public void addRefreshTokenResponseCookie(HttpServletResponse response, String token) {
		response.addHeader(SET_COOKIE, createRefreshTokenResponseCookie(token).toString());
	}

	public ResponseCookie createAccessTokenResponseCookie(String token) {
		return ResponseCookie.from(accessTokenName, token)
			.path("/")
			.httpOnly(true)
			.sameSite("None")
			.secure(true)
			.domain(cookieDomain)
			.maxAge(accessTokenExpirationSecond / 1000)
			.build();
	}

	public ResponseCookie createRefreshTokenResponseCookie(String token) {
		return ResponseCookie.from(refreshTokenName, token)
			.path("/")
			.httpOnly(true)
			.sameSite("None")
			.secure(true)
			.domain(cookieDomain)
			.maxAge(refreshTokenExpirationSecond / 1000)
			.build();
	}




}
