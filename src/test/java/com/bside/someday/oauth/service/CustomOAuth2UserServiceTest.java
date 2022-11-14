package com.bside.someday.oauth.service;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	OAuth2UserRequest oAuth2UserRequest;

	CustomOAuth2UserService customOAuth2UserService;

	@BeforeEach
	void init() {
		customOAuth2UserService = new CustomOAuth2UserService(userRepository);
	}

	@Test
	void loadUser() {

		// given

		// when
		OAuth2User oAuth2User = customOAuth2UserService.loadUser(oAuth2UserRequest);

		// then
	}

	@Test
	void valueOf() {

		log.info("{}", SocialType.valueOf("kakao".toUpperCase()));

	}

	@Test
	void saveOrUpdate() {

		// userRepository.findUserBySocialIdAndSocialType

		// userRepository.save

		Map<String, Object> userMap = customOAuth2UserService.saveOrUpdate(null, null, null);

	}
}