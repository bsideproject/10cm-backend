package com.bside.someday.cache.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenRedisKey {
	TOKEN_KEY("refresh:token");

	private final String value;
}
