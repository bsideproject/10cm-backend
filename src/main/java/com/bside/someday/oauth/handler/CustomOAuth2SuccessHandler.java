package com.bside.someday.oauth.handler;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.bside.someday.cache.token.TokenRedisCacheService;
import com.bside.someday.common.util.CookieUtil;
import com.bside.someday.oauth.CustomOauth2User;
import com.bside.someday.oauth.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${client.redirectUrl}")
	private String redirectUrl;
	@Value("${authentication.cookie.accessTokenName}")
	private String accessTokenName;
	@Value("${authentication.cookie.refreshTokenName}")
	private String refreshTokenName;
	private final JwtTokenProvider jwtTokenProvider;
	private final TokenRedisCacheService tokenRedisCacheService;
	private final CookieUtil cookieUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		CustomOauth2User oauth2User = (CustomOauth2User)authentication.getPrincipal();

		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

		tokenRedisCacheService.update(oauth2User.getUserId().toString(), refreshToken,
			jwtTokenProvider.getRefreshTokenExpirationSecond());

		// JWT 쿠키로 전달
		cookieUtil.addAccessTokenResponseCookie(response, accessToken);
		cookieUtil.addRefreshTokenResponseCookie(response, refreshToken);

		String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
			// JWT Query Param 전달
			.queryParam(accessTokenName, accessToken)
			.queryParam(refreshTokenName, refreshToken)
			.build().toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);

	}

}
