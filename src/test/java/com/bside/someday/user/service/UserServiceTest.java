package com.bside.someday.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bside.someday.common.util.ClientUtil;
import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.storage.service.StorageService;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.dto.request.UserProfileRequestDto;
import com.bside.someday.user.dto.response.UserProfileResponseDto;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private StorageService storageService;

	@Mock
	private ClientUtil clientUtil;

	private UserService userService;

	@BeforeEach
	void setup() {
		userService = new UserService(storageService, userRepository, clientUtil);
	}

	@Test
	void 프로필_조회_성공() {

		// given
		User user1 = User.builder()
			.userId(1L)
			.name("테스트")
			.nickname("닉네임")
			.socialId("adsfasdfi")
			.socialType(SocialType.KAKAO)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		//when
		User findUser = userService.findOneById(1L);

		//then
		assertThat(findUser.getUserId()).isEqualTo(1L);
		assertThrows(UserNotFoundException.class, () -> userService.findOneById(2L));

	}

	@Test
	void 사용자_프로필_업데이트() {

		//given
		User beforeUser = User.builder()
			.userId(1L)
			.name("테스트")
			.nickname("닉네임")
			.socialId("adsfasdfi")
			.socialType(SocialType.KAKAO)
			.build();

		String afterNickname = "변경후닉네임";
		String afterImageProfile = "/image/adfsdfasd";

		User afterUser = User.builder()
			.userId(1L)
			.name("테스트")
			.nickname(afterNickname)
			.socialId("adsfasdfi")
			.socialType(SocialType.KAKAO)
			.profileImage(afterImageProfile)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(beforeUser));
		when(userRepository.save(any())).thenReturn(afterUser);

		//when
		UserProfileRequestDto requestDto = new UserProfileRequestDto(afterNickname, afterImageProfile);
		UserProfileResponseDto responseDto = userService.updateUser(1L, requestDto);

		//then
		assertThat(responseDto.getNickname()).isEqualTo(afterNickname);
		assertThat(responseDto.getProfileImageUrl()).isEqualTo(afterImageProfile);

	}

	@Test
	void 사용자_탈퇴_실패() {

		//given
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		//then
		assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));

	}

	@Test
	void 프로필_조회_실패() {

		// given
		User user1 = User.builder()
			.userId(1L)
			.name("테스트")
			.nickname("닉네임")
			.socialId("adsfasdfi")
			.socialType(SocialType.KAKAO)
			.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		when(userRepository.findById(2L)).thenReturn(Optional.empty());

		//when
		UserProfileResponseDto requestDto = userService.findUser(1L);

		//then
		assertThat(requestDto.getUserId()).isEqualTo(1L);
		assertThrows(UserNotFoundException.class, () -> userService.findUser(2L));

	}

}
