package com.bside.someday.oauth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.bside.someday.oauth.CustomOauth2User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	//TODO. client url로 변경
	private static final String url = "http://localhost:8080/social";

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		CustomOauth2User oauth2User = (CustomOauth2User)authentication.getPrincipal();

		//TODO. JWT 발급 및 적재


		String targetUrl = UriComponentsBuilder.fromUriString(url)

			//TODO. 테스트용 추후 삭제
			.queryParam("socialId", authentication.getName())
			.queryParam("id", oauth2User.getUserId())

			.build()
			.toUriString();

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
