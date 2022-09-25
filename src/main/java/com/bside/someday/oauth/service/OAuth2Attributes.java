package com.bside.someday.oauth.service;

import java.util.Map;

import com.bside.someday.user.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OAuth2Attributes {

	private static final String KAKAO = "kakao";
	private static final String NAVER = "naver";

	private final Map<String, Object> attributes;
	private final String nameAttributeKey;
	private final String oauthId;
	private final String nickname;
	private final String email;
	private final String profileImage;

	@Builder
	public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String oauthId, String nickname,
		String email, String profileImage) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.oauthId = oauthId;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
	}

	public static OAuth2Attributes of(String registrationId, String nameAttributeKey,
		Map<String, Object> attributes) {
		switch (registrationId) {
			case "kakao":
				return ofKakao(nameAttributeKey, attributes);
			default:
				throw new IllegalArgumentException("현재 지원하지 않는 소셜 로그인입니다.");
		}
	}

	private static OAuth2Attributes ofKakao(String nameAttributeKey, Map<String, Object> attributes) {

		Map<String, Object> account = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)account.get("profile");

		return OAuth2Attributes.builder()
			.oauthId(attributes.get(nameAttributeKey).toString())
			.nickname((String)profile.get("nickname"))
			.email((String)account.get("email"))
			.attributes(attributes)
			.nameAttributeKey(nameAttributeKey)
			.profileImage((String)profile.get("profile_image_url"))
			.build();
	}

	public User toEntity(String socialId, String registrationId, String nickname) {
		return User.builder()
			.email(email)
			.socialId(socialId)
			.socialType(registrationId)
			.nickname(nickname)
			.build();
	}
}
