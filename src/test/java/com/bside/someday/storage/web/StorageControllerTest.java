package com.bside.someday.storage.web;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.error.exception.storage.FileNotFoundException;
import com.bside.someday.oauth.service.JwtTokenProvider;
import com.bside.someday.storage.repository.StorageRepository;
import com.bside.someday.storage.service.StorageService;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class StorageControllerTest {

	@Autowired
	StorageRepository storageRepository;

	@Autowired
	StorageService storageService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private MockMvc mvc;

	private String accessToken;

	@BeforeEach
	void setup() {
		User user1 = User.builder()
			.name("sdfgsdf")
			.email("asdfasdf")
			.nickname("dsfsdf")
			.profileImage("")
			.socialId("123123")
			.socialType("kakao")
			.build();

		accessToken = jwtTokenProvider.createAccessToken(userRepository.save(user1));

	}

	@Test
	void 파일_업로드() {

		//given
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.txt",
			MediaType.TEXT_PLAIN_VALUE, "test_file1".getBytes(StandardCharsets.UTF_8));

		//when
		String fileName = storageService.uploadFile(file1).getName();

		//then
		assertThat(storageService.findOneByName(fileName).getName()).isEqualTo(fileName);
	}

	@Test
	void 파일_다운로드() throws Exception {

		//given
		String content = "asnldfkasdnflknasdkfasdfsdalnfkasdnflk";
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes(StandardCharsets.UTF_8));

		//when
		String fileName = storageService.uploadFile(file1).getName();

		//then
		MvcResult result = mvc.perform(get("/api/v1/resources/" + fileName + "/download"))
			.andExpect(status().is(200)).andReturn();

		assertThat(result.getResponse().getContentAsByteArray()).isNotNull();
		assertThat(result.getResponse().getContentAsString()).isEqualTo(content);

	}

	@Test
	void 파일_리소스_조회() throws Exception {

		//given
		String content = "asnldfkasdnflknasdkfasdfsdalnfkasdnflk";
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes(StandardCharsets.UTF_8));

		//when
		String fileName = storageService.uploadFile(file1).getName();

		MvcResult result = mvc.perform(get("/api/v1/resources/" + fileName))
			.andExpect(status().is(200)).andReturn();

		//then
		assertThat(result.getResponse().getContentAsByteArray()).isNotNull();
		assertThat(result.getResponse().getContentAsString()).isEqualTo(content);

	}

	@Test
	void 파일_삭제() {

		//given
		String content = "asnldfkasdnflknasdkfasdfsdalnfkasdnflk";
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes(StandardCharsets.UTF_8));

		//when
		String fileName = storageService.uploadFile(file1).getName();
		storageService.deleteFile(null, fileName);

		//then
		assertThrows(FileNotFoundException.class, () -> storageService.findOneByName(fileName));

	}

	@Test
	void 파일_업로드_성공() throws Exception {

		//given
		String content = "asnldfkasdnflknasdkfasdfsdalnfkasdnflk";
		MockMultipartFile file1 = new MockMultipartFile("file", "upload_test.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes(StandardCharsets.UTF_8));

		//when
		MvcResult result = mvc.perform(
			multipart("/api/v1/resources")
				.file(file1)
				.header(AUTHORIZATION, "Bearer " + accessToken)
		).andExpect(status().is(201)).andReturn();

		//then
		mvc.perform(get(result.getResponse().getContentAsString()))
			.andExpect(status().is(200));
	}

	@Test
	void 파일_업로드_실패() throws Exception {

		mvc.perform(
			multipart("/api/v1/resources")
				.header(AUTHORIZATION, "Bearer " + accessToken)
		).andExpect(status().is(ErrorType.FILE_BAD_REQUEST.getStatus().value()));

	}
}
