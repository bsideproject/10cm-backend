package com.bside.someday.user.web;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.storage.service.StorageService;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.user.dto.UserProfileRequestDto;
import com.bside.someday.user.dto.UserProfileResponseDto;
import com.bside.someday.user.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api("Auth API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {

	private final UserService userService;
	private final StorageService storageService;

	@ApiOperation("회원 프로필 조회")
	@GetMapping
	public ResponseEntity<UserProfileResponseDto> findUser(@AuthUser UserInfo userInfo) {

		UserProfileResponseDto responseDto = userService.findUser(userInfo.getUserId());

		return ResponseDto.ok(responseDto);
	}

	@ApiOperation("회원 프로필 수정")
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity updateUser(@AuthUser UserInfo userInfo,
		@Valid @RequestPart(value = "user") UserProfileRequestDto requestDto,
		@RequestPart(value = "image", required = false) MultipartFile multipartFile) {

		if (multipartFile != null) {
			String url = storageService.uploadFile(multipartFile).getUrl();
			requestDto.setProfileImageUrl(url);
		}

		userService.updateUser(userInfo.getUserId(), requestDto);

		return ResponseDto.ok(null);
	}

	@ApiOperation("회원 탈퇴")
	@DeleteMapping
	public ResponseEntity deleteUser(@AuthUser UserInfo userInfo) {

		userService.deleteUser(userInfo.getUserId());
		return ResponseDto.ok(null);
	}
}
