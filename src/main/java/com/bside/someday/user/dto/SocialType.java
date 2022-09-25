package com.bside.someday.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {

	KAKAO("kakao"),
	NAVER("naver");

	private final String provideServer;

}
