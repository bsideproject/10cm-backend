package com.bside.someday.oauth.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.oauth.dto.TokenDto;
import com.bside.someday.oauth.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api("Auth API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthController {
	private final AuthService authService;

	@ApiOperation("refresh 토큰발급")
	@PostMapping("/refresh")
	public ResponseEntity<TokenDto> refreshToken(
		@RequestHeader(value = "refresh") String refreshToken
	) {
		TokenDto tokenDto = authService.refresh(refreshToken);
		return ResponseDto.ok(tokenDto);
	}

	//TODO: 로그아웃 기능 구현 필요
}
