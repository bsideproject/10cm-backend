package com.bside.someday.user.web;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.oauth.service.JwtTokenProvider;
import com.bside.someday.user.dto.UserProfileRequestDto;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;
import com.bside.someday.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private MockMvc mvc;


	@Test
	void 사용자_프로필_조회() throws Exception {

		//given
		User user1 = User.builder()
			.name("nameTest")
			.email("asdf@email.com")
			.nickname("nicknameTest")
			.profileImage("")
			.socialId("sfadfadasdasdf")
			.socialType("kakao")
			.build();

		//when
		String accessToken = jwtTokenProvider.createAccessToken(userRepository.save(user1));

		//then
		mvc.perform(get("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION, "Bearer " + accessToken))
			.andDo(print())
			.andExpect(status().isOk());

	}

	@Test
	void 사용자_프로필_수정() throws Exception {

		//given
		String afterNickname = "afterNickname";

		User user1 = User.builder()
			.name("test1")
			.email("asdf@email.com")
			.nickname("beforeNickname")
			.profileImage("")
			.socialId("asdfadfadsf")
			.socialType("kakao")
			.build();

		String accessToken = jwtTokenProvider.createAccessToken(userRepository.save(user1));

		UserProfileRequestDto requestDto = new UserProfileRequestDto(user1.getUserId(), afterNickname, "");

		ObjectMapper objectMapper = new ObjectMapper();
		String contentJSON = objectMapper.writeValueAsString(requestDto);

		//when
		mvc.perform(put("/api/v1/user")
			.header(AUTHORIZATION, "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.content(contentJSON)
		).andExpect(status().isOk());

		//then
		assertThat(userService.findUser(user1.getUserId()).getNickname()).isEqualTo(afterNickname);

	}

	@Test
	void 사용자_탈퇴() throws Exception {

		//given
		User user1 = User.builder()
			.name("sdfgsdf")
			.email("asdfasdf")
			.nickname("dsfsdf")
			.profileImage("")
			.socialId("123123")
			.socialType("kakao")
			.build();

		//when
		mvc.perform(delete("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION, "Bearer " + jwtTokenProvider.createAccessToken(userRepository.save(user1))))
			.andExpect(status().isOk());

		//then
		assertThrows(UserNotFoundException.class, () -> userService.findUser(user1.getUserId()));

	}
}
