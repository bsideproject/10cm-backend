package com.bside.someday.user.service;

import org.springframework.stereotype.Service;

import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.user.dto.UserProfileRequestDto;
import com.bside.someday.user.dto.UserProfileResponseDto;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User findOneById(Long userId) {
		return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
	}

	public UserProfileResponseDto updateUser(Long userId, UserProfileRequestDto requestDto) {

		User user = findOneById(userId).update(requestDto.getNickname(), requestDto.getProfileImageUrl());

		return new UserProfileResponseDto(userRepository.save(user));
	}

	public void deleteUser(Long userId) {

		if (userRepository.findById(userId).isPresent()) {
			userRepository.deleteById(userId);
		} else {
			throw new UserNotFoundException();
		}

	}

	public UserProfileResponseDto findUser(Long userId) {

		User user = findOneById(userId);

		return new UserProfileResponseDto(user);
	}
}
