
package com.bside.someday.oauth.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.bside.someday.cache.token.TokenRedisCacheService;
import com.bside.someday.common.util.CookieUtil;
import com.bside.someday.error.exception.oauth.TokenExpiredException;
import com.bside.someday.error.exception.oauth.TokenInvalidException;
import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.oauth.dto.TokenDto;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final TokenRedisCacheService tokenRedisCacheService;
	private final CookieUtil cookieUtil;

	public TokenDto refresh(HttpServletResponse response, String refreshToken) {

		User user = getUserFromRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.createAccessToken(user);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

		tokenRedisCacheService.update(String.valueOf(user.getUserId()), newRefreshToken,
			jwtTokenProvider.getRefreshTokenExpirationSecond());

		cookieUtil.addAccessTokenResponseCookie(response, newAccessToken);
		cookieUtil.addRefreshTokenResponseCookie(response, newRefreshToken);

		return new TokenDto(newAccessToken, newRefreshToken);
	}

	public TokenDto access(HttpServletResponse response, String refreshToken) {

		User user = getUserFromRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.createAccessToken(user);

		cookieUtil.addAccessTokenResponseCookie(response, newAccessToken);
		return new TokenDto(newAccessToken, refreshToken);
	}

	private User getUserFromRefreshToken(String refreshToken) {

		if (!jwtTokenProvider.validate(refreshToken)) {
			throw new TokenInvalidException();
		}

		Long userId = jwtTokenProvider.getUserId(refreshToken);
		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		String storedToken = tokenRedisCacheService.get(userId);

		if (!jwtTokenProvider.validate(storedToken)) {
			throw new TokenExpiredException();
		}

		if (!refreshToken.equals(storedToken)) {
			throw new TokenInvalidException();
		}
		return user;
	}
}
