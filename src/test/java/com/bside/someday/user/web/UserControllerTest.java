package com.bside.someday.user.web;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.bside.someday.security.WithMockCustomUser;
import com.bside.someday.user.dto.request.UserProfileRequestDto;
import com.bside.someday.user.dto.response.UserProfileResponseDto;
import com.bside.someday.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NonAsciiCharacters")
class UserControllerTest {

	@Autowired
	private UserService userService;

	private ObjectMapper objectMapper;

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	DataSource dataSource;

	@BeforeAll
	public void init() {
		try (Connection conn = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(conn, new ClassPathResource("/db/h2/data.sql"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeEach
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
		this.objectMapper = new ObjectMapper();
	}

	@Test
	@WithMockCustomUser
	void 사용자_프로필_조회_성공() throws Exception {
		mvc.perform(get("/api/v1/user"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void 사용자_프로필_조회_실패_권한없음() throws Exception {
		mvc.perform(get("/api/v1/user"))
			.andExpect(status().is4xxClientError())
			.andDo(print());

	}

	@Test
	@WithMockCustomUser
	void 프로필_수정_성공() throws Exception {

		//given
		String afterNickname = "변경후닉네임";
		UserProfileRequestDto requestDto = new UserProfileRequestDto(afterNickname, "");

		String contentJSON = objectMapper.writeValueAsString(requestDto);

		//when

		MvcResult result = mvc.perform(put("/api/v1/user")
			.contentType(MediaType.APPLICATION_JSON)
			.content(contentJSON)
		).andExpect(status().isOk()).andReturn();

		//then
		UserProfileResponseDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(),
			UserProfileResponseDto.class);

		assertThat(userService.findUser(responseDto.getUserId()).getNickname()).isEqualTo(afterNickname);
	}


	@ParameterizedTest
	@WithMockCustomUser
	@ValueSource(strings = {"", "anflkandsfklansdkfnklsandlfnlansdfkasndf", "#)@*)!(@#AF", "TEST1!"})
	void 프로필_수정_실패(String nickname) throws Exception {

		//when
		String requestStr = "{\"nickname\":\"" + nickname + "\"}";

		mvc.perform(put("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestStr))
			.andExpect(status().is4xxClientError());

	}

	@Test
	@WithMockCustomUser
	void 닉네임_수정_성공() throws Exception {

		//given
		String afterNickname = "변경닉네임2";
		String requestStr = "{\"nickname\":\"" + afterNickname + "\"}";

		//when
		MvcResult result = mvc.perform(post("/api/v1/user/profile/nickname-update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestStr))
			.andExpect(status().isOk()).andReturn();


		//then
		UserProfileResponseDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(),
			UserProfileResponseDto.class);

		assertThat(userService.findUser(responseDto.getUserId()).getNickname()).isEqualTo(afterNickname);

	}

	@ParameterizedTest
	@WithMockCustomUser
	@ValueSource(strings = {"", "anflkandsfklansdkfnklsandlfnlansdfkasndf", "#)@*)!(@#AF", "TEST1!", "띄어쓰기 안댐"})
	void 닉네임_수정_실패(String nickname) throws Exception {

		//when
		String requestStr = "{\"nickname\":\"" + nickname + "\"}";

		mvc.perform(post("/api/v1/user/profile/nickname-update")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestStr))
			.andExpect(status().is4xxClientError());

	}

	@Test
	@WithMockCustomUser(id = 2L)
	void 사용자_탈퇴() throws Exception {

		MvcResult result = mvc.perform(get("/api/v1/user"))
			.andExpect(status().isOk())
			.andReturn();

		UserProfileResponseDto responseDto = objectMapper.readValue(result.getResponse().getContentAsString(),
			UserProfileResponseDto.class);

		//when
		mvc.perform(delete("/api/v1/user")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		//then
		assertThat(userService.findOneById(responseDto.getUserId()).getName()).isNullOrEmpty();
		assertThat(userService.findOneById(responseDto.getUserId()).getEmail()).isNullOrEmpty();
		assertThat(userService.findOneById(responseDto.getUserId()).getSocialId()).isNullOrEmpty();
	}
}
