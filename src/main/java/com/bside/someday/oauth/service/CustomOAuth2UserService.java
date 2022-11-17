package com.bside.someday.oauth.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bside.someday.oauth.CustomOauth2User;
import com.bside.someday.oauth.dto.OAuth2Attributes;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		// 로그인 서비스 구분 ID
		String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();

		// OAuth2 로그인 필드 값
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
			.getUserNameAttributeName();

		OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName,
			oAuth2User.getAttributes());

		String socialId = getSocialUniqueId(oAuth2User.getName(), registrationId);

		Map<String, Object> userMap = saveOrUpdate(attributes, socialId, registrationId);

		Long userId = (Long)userMap.get("userId");

		CustomOauth2User customOauth2User = new CustomOauth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			attributes.getAttributes(), attributes.getNameAttributeKey());
		customOauth2User.setUserId(userId);

		return customOauth2User;

	}

	@Transactional
	public Map<String, Object> saveOrUpdate(OAuth2Attributes attributes, String socialId, String registrationId) {

		Map<String, Object> userMap = new HashMap<>();
		Optional<User> user = userRepository.findUserBySocialIdAndSocialType(socialId,
			SocialType.valueOf(registrationId.toUpperCase()));

		if (user.isPresent()) {
			userMap.put("userId", user.get().getUserId());
			userMap.put("email", user.get().getEmail());
			userMap.put("name", user.get().getName());
			userMap.put("nickname", user.get().getNickname());
			userMap.put("profileImage", user.get().getProfileImage());

			if ("N".equals(user.get().getProfileUploadYn())) {
				userRepository.save(user.get().update(user.get().getNickname(), attributes.getProfileImage()));
			}

			return userMap;
		}

		String nickname = getRandomNickName(attributes);

		User saveUser = userRepository.save(
			attributes.toEntity(socialId, registrationId, nickname, attributes.getName(), attributes.getProfileImage()));

		userMap.put("userId", saveUser.getUserId());
		userMap.put("email", saveUser.getEmail());
		userMap.put("nickname", saveUser.getNickname());
		userMap.put("name", saveUser.getName());
		userMap.put("profileImage", saveUser.getProfileImage());

		return userMap;
	}

	private String getSocialUniqueId(String id, String registrationId) {
		if ("kakao".equals(registrationId)) {
			return id;
		}
		throw new IllegalArgumentException("현재 지원하지 않는 소셜 로그인입니다.");
	}

	private String getRandomNickName(OAuth2Attributes attributes) {

		if (attributes != null && StringUtils.hasText(attributes.getNickname())) {
			return attributes.getNickname();
		}

		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		return String.format("USER%d", random.nextInt(100_000));
	}
}
