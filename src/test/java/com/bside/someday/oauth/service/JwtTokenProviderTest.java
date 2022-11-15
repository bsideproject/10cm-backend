package com.bside.someday.oauth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import com.bside.someday.oauth.CustomOauth2User;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;

@ActiveProfiles(profiles = "local")
@SpringBootTest(classes = {JwtTokenProvider.class})
class JwtTokenProviderTest {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Test
	@DisplayName("토큰 create 테스트 - 성공")
	void createAccessToken() {

		// given
		User user = User.builder()
			.userId(3L)
			.socialType(SocialType.KAKAO)
			.build();

		// when
		String accessToken = jwtTokenProvider.createAccessToken(user);
		Long tokenUserId = jwtTokenProvider.getUserId(accessToken);

		// then
		assertEquals(user.getUserId(), tokenUserId);

	}

	@Test
	@DisplayName("토큰 validate 테스트 - 성공")
	void validate() {
		// given
		User user = User.builder()
			.userId(3L)
			.socialType(SocialType.KAKAO)
			.build();

		// when
		String accessToken = jwtTokenProvider.createAccessToken(user);

		// then
		assertThat(jwtTokenProvider.validate(accessToken)).isTrue();

	}
}