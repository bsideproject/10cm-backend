package com.bside.someday.oauth.service;

import static com.bside.someday.oauth.filter.JwtAuthenticationFilter.*;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bside.someday.error.exception.oauth.TokenInvalidException;
import com.bside.someday.oauth.CustomOauth2User;
import com.bside.someday.user.entity.Role;
import com.bside.someday.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	@Value("${authentication.jwt.secretKey}")
	private String secretKey;

	@Value("${authentication.jwt.accessTokenExpirationSecond}")
	private Long accessTokenExpirationSecond;

	@Value("${authentication.jwt.refreshTokenExpirationSecond}")
	private Long refreshTokenExpirationSecond;

	@Value("${authentication.cookie.accessTokenName}")
	private String accessTokenName;

	//TODO: 사용자 권한


	public String getJwtToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (accessTokenName.equals(cookie.getName())) {
					String accessToken = cookie.getValue();
					return accessToken;
				}
			}
		}
		return null;
	}

	public String createAccessToken(Authentication authentication) {

		CustomOauth2User oauth2User = (CustomOauth2User)authentication.getPrincipal();

		return Jwts.builder()
			.setSubject(String.valueOf(oauth2User.getUserId()))
			.claim("role", Role.USER)
			.setIssuedAt(new Date())
			.setExpiration(getExpiredDate(accessTokenExpirationSecond))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
				SignatureAlgorithm.HS512)
			.compact();
	}

	public String createAccessToken(User user) {

		return Jwts.builder()
			.setSubject(String.valueOf(user.getUserId()))
			.claim("role", Role.USER)
			.setIssuedAt(new Date())
			.setExpiration(getExpiredDate(accessTokenExpirationSecond))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
				SignatureAlgorithm.HS512)
			.compact();
	}

	public String createRefreshToken(Authentication authentication) {

		CustomOauth2User oauth2User = (CustomOauth2User)authentication.getPrincipal();

		return Jwts.builder()
			.setSubject(String.valueOf(oauth2User.getUserId()))
			.setIssuedAt(new Date())
			.setExpiration(getExpiredDate(refreshTokenExpirationSecond))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
				SignatureAlgorithm.HS512)
			.compact();
	}

	public String createRefreshToken(User user) {

		return Jwts.builder()
			.setSubject(String.valueOf(user.getUserId()))
			.setIssuedAt(new Date())
			.setExpiration(getExpiredDate(refreshTokenExpirationSecond))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
				SignatureAlgorithm.HS512)
			.compact();
	}

	public boolean validate(String token) {

		if (!StringUtils.hasText(token)) {
			return false;
		}

		try {
			return getClaims(token)
				.getExpiration()
				.after(new Date());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}

	public Authentication getAuthentication(String token) {
		return new UsernamePasswordAuthenticationToken(
			getUserId(token),
			null,
			getAuthorities(token)
		);
	}

	public Long getUserId(String token) {
		return Long.parseLong(getClaims(token).getSubject());
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public Collection<? extends GrantedAuthority> getAuthorities(String token) {
		Claims claims = getClaims(token);
		return Collections.singletonList(
			new SimpleGrantedAuthority(claims.get("role").toString()) // get("USER")
		);
	}

	public Date getExpiredDate(Long expirationSecond) {
		Date now = new Date();
		return new Date(now.getTime() + expirationSecond);
	}



	public long getRefreshTokenExpirationSecond() {
		return this.refreshTokenExpirationSecond;
	}

	public String resolveToken(String bearerHeader) {

		if (!StringUtils.hasText(bearerHeader) || !bearerHeader.startsWith(BEARER_TOKEN_PREFIX)) {
			throw new TokenInvalidException();
		}

		String accessToken = bearerHeader.substring(BEARER_TOKEN_PREFIX_LENGTH);

		if (!validate(accessToken)) {
			throw new TokenInvalidException();
		}

		return accessToken;
	}

}
