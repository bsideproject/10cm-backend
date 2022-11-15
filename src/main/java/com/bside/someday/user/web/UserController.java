package com.bside.someday.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.user.dto.UserProfileRequestDto;
import com.bside.someday.user.dto.UserProfileResponseDto;
import com.bside.someday.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "User API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

	private final UserService userService;

	@ApiOperation("회원 프로필 조회")
	@GetMapping
	public ResponseEntity<?> findUser(@AuthUser UserInfo userInfo) {

		UserProfileResponseDto responseDto = userService.findUser(userInfo.getUserId());

		return ResponseDto.ok(responseDto);
	}

	@ApiOperation("회원 프로필 수정")
	@PutMapping
	public ResponseEntity<?> updateUser(@AuthUser UserInfo userInfo,
		@RequestBody UserProfileRequestDto requestDto) {
		return ResponseDto.ok(userService.updateUser(userInfo.getUserId(), requestDto).getUserId());
	}

	@ApiOperation("회원 탈퇴")
	@DeleteMapping
	public ResponseEntity<?> deleteUser(@AuthUser UserInfo userInfo) {

		userService.deleteUser(userInfo.getUserId());
		return ResponseDto.ok(null);
	}
}
