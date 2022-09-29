package com.bside.someday.cache.token;

import static com.bside.someday.cache.token.TokenRedisKey.*;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRedisCacheService {

	private final RedisTemplate<String, String> redisTemplate;

	public String get(String token) {
		return redisTemplate.opsForValue().get(token);
	}

	public String get(Long userId) {
		return redisTemplate.opsForValue().get(TOKEN_KEY.getValue() + ":" + userId);
	}

	public void update(String key, String value, long expirationSecond) {
		redisTemplate.opsForValue().set(TOKEN_KEY.getValue() + ":" + key, value, expirationSecond, TimeUnit.MILLISECONDS);
	}

	public void delete(Long userId) {
		redisTemplate.delete(TOKEN_KEY.getValue() + ":" + userId);
	}
}
