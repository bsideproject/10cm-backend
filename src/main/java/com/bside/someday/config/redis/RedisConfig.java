package com.bside.someday.config.redis;

import static java.time.Duration.*;

import java.time.Duration;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableCaching
public class RedisConfig {

	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

		log.info("redisConnectionFactory >>> {} {}", redisProperties.getHost(), redisProperties.getPort());
		return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		log.info("redisTemplate");

		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());

		return redisTemplate;
	}

	@Bean
	public CacheManager somedayCacheManager() {
		log.info("somedayCacheManager");

		var stringSerializationPair = RedisSerializationContext
			.SerializationPair.fromSerializer(new StringRedisSerializer());
		var objectSerializationPair = RedisSerializationContext
			.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer());

		var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(stringSerializationPair)
			.serializeValuesWith(objectSerializationPair)
			.entryTtl(CachingDuration.HOUR.duration);

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory())
			.cacheDefaults(redisCacheConfiguration).build();
	}

	@Getter
	@RequiredArgsConstructor
	enum CachingDuration {

		DAY(ofDays(1L)),
		HOUR(ofHours(1L));

		private final Duration duration;
	}
}
