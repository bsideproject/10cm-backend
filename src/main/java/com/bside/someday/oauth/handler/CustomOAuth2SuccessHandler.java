package com.bside.someday.oauth.handler;

import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.bside.someday.cache.token.TokenRedisCacheService;
import com.bside.someday.common.util.CookieUtil;
import com.bside.someday.oauth.CustomOauth2User;
import com.bside.someday.oauth.service.CustomOAuth2UserService;
import com.bside.someday.oauth.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${client.redirectUrl}")
	private String redirectUrl;

	private final JwtTokenProvider jwtTokenProvider;
	private final TokenRedisCacheService tokenRedisCacheService;
	private final CookieUtil cookieUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		CustomOauth2User oauth2User = (CustomOauth2User)authentication.getPrincipal();

		//TODO: EMAIL 중복 체크

		String accessToken = jwtTokenProvider.createAccessToken(authentication);
		String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

		tokenRedisCacheService.update(oauth2User.getUserId().toString(), refreshToken,
			jwtTokenProvider.getRefreshTokenExpirationSecond());

		cookieUtil.addAccessTokenResponseCookie(response, accessToken);
		cookieUtil.addRefreshTokenResponseCookie(response, refreshToken);

		String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
			.build()
			.toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

}
