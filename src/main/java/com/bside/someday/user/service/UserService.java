package com.bside.someday.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.common.util.ClientUtil;
import com.bside.someday.error.exception.oauth.UserInvalidParameterException;
import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.storage.service.StorageService;
import com.bside.someday.user.dto.request.UserNicknameRequestDto;
import com.bside.someday.user.dto.request.UserProfileRequestDto;
import com.bside.someday.user.dto.response.UserNicknameResponseDto;
import com.bside.someday.user.dto.response.UserProfileResponseDto;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final StorageService storageService;
	private final UserRepository userRepository;
	private final ClientUtil clientUtil;

	@Transactional
	public User findOneById(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}

	@Transactional
	public UserProfileResponseDto updateUser(Long userId, UserProfileRequestDto requestDto) {

		User user = findOneById(userId)
			.updateNickName(requestDto.getNickname())
			.updateProfileImage(requestDto.getProfileImageUrl());

		return new UserProfileResponseDto(userRepository.save(user));
	}

	@Transactional
	public void deleteUser(Long userId) {
		User user = findOneById(userId);
		clientUtil.requestUnlink(user.getSocialId(), user.getSocialType());
		userRepository.save(user.delete());
	}

	@Transactional
	public UserProfileResponseDto findUser(Long userId) {

		User user = findOneById(userId);

		return new UserProfileResponseDto(user);
	}

	@Transactional
	public UserProfileResponseDto updateProfileImage(Long userId, MultipartFile multipartFile) {

		User user = findOneById(userId);

		user.updateProfileImage(storageService.uploadFile(multipartFile).getUrl());

		return new UserProfileResponseDto(userRepository.save(user));
	}

	@Transactional
	public UserProfileResponseDto updateUserNickname(Long userId, UserNicknameRequestDto requestDto) {
		User user = findOneById(userId);
		user.update(requestDto.getNickname(), user.getProfileImage());
		return new UserProfileResponseDto(userRepository.save(user));
	}

	@Transactional
	public UserNicknameResponseDto getNicknameDuplicated(UserNicknameRequestDto requestDto) {

		if (requestDto == null) {
			throw new UserInvalidParameterException();
		}

		return new UserNicknameResponseDto(requestDto.getNickname(),
			userRepository.existsByNickname(requestDto.getNickname()));
	}
}
