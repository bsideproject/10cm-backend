package com.bside.someday.user.web;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.error.exception.oauth.UnAuthorizedException;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.user.dto.request.UserNicknameRequestDto;
import com.bside.someday.user.dto.request.UserProfileRequestDto;
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
		if (userInfo == null) {
			throw new UnAuthorizedException();
		}
		return ResponseDto.ok(userService.findUser(userInfo.getUserId()));
	}

	@ApiOperation("회원 프로필 수정")
	@PutMapping
	public ResponseEntity<?> updateUser(@AuthUser UserInfo userInfo,
		@Valid @RequestBody UserProfileRequestDto requestDto) {

		if (userInfo == null) {
			throw new UnAuthorizedException();
		}

		return ResponseDto.ok(userService.updateUser(userInfo.getUserId(), requestDto));
	}
	@ApiOperation("프로필 이미지 수정")
	@PostMapping(value = "/profile/image-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> updateProfileImage(@AuthUser UserInfo userInfo,
		@RequestPart(value = "file", required = false) MultipartFile multipartFile) {

		return ResponseDto.ok(userService.updateProfileImage(userInfo.getUserId(), multipartFile));
	}

	@ApiOperation("프로필 닉네임 수정")
	@PostMapping("/profile/nickname-update")
	public ResponseEntity<?> updateUserNickname(@AuthUser UserInfo userInfo,
		@Valid @RequestBody UserNicknameRequestDto requestDto) {
		return ResponseDto.ok(userService.updateUserNickname(userInfo.getUserId(), requestDto));
	}

	@ApiOperation("프로필 닉네임 중복체크")
	@GetMapping("/profile/nickname-check")
	public ResponseEntity<?> getNicknameDuplicated(@Valid UserNicknameRequestDto requestDto) {

		return ResponseDto.ok(userService.getNicknameDuplicated(requestDto));
	}

	@ApiOperation("회원 탈퇴")
	@DeleteMapping
	public ResponseEntity<?> deleteUser(@AuthUser UserInfo userInfo) {

		userService.deleteUser(userInfo.getUserId());

		return ResponseDto.ok(null);
	}
}
