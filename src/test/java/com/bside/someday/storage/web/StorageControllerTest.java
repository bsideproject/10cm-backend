package com.bside.someday.storage.web;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.bside.someday.error.exception.storage.FileNotFoundException;
import com.bside.someday.oauth.service.JwtTokenProvider;
import com.bside.someday.security.WithMockCustomUser;
import com.bside.someday.storage.repository.StorageRepository;
import com.bside.someday.storage.service.StorageService;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = RANDOM_PORT)
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
	private MockMvc mvc;

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	private String accessToken;

	@BeforeEach
	void setup() {
		User user = userRepository.save(User.builder()
			.userId(1L)
			.name("테스트")
			.email("asdfasdf@test.com")
			.nickname("테스트닉네임11")
			.profileImage("")
			.socialId("123123")
			.socialType(SocialType.KAKAO)
			.build());

		accessToken = jwtTokenProvider.createAccessToken(user);
	}

	@Test
	@WithMockCustomUser
	void 파일_다운로드() throws Exception {

		//given
		String content = "asnldfkasdnflknasdkfasdfsdalnfkasdnflk";
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.png",
			MediaType.IMAGE_PNG_VALUE, content.getBytes(StandardCharsets.UTF_8));

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
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.jpeg",
			MediaType.IMAGE_JPEG_VALUE, content.getBytes(StandardCharsets.UTF_8));

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
		MockMultipartFile file1 = new MockMultipartFile("file", "test_file.png",
			MediaType.IMAGE_PNG_VALUE, content.getBytes(StandardCharsets.UTF_8));

		//when
		String fileName = storageService.uploadFile(file1).getName();
		storageService.deleteFile(null, fileName);

		//then
		assertThrows(FileNotFoundException.class, () -> storageService.findOneByName(fileName));

	}

	@Test
	void 파일_업로드_성공() {

		byte[] bytes = new byte[1_024];
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

		MockMultipartFile file1 = new MockMultipartFile("file", "test.jpeg",
			MediaType.IMAGE_JPEG_VALUE, bytes);

		parameters.add("file", file1.getResource());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

		ResponseEntity<Object> response = testRestTemplate.postForEntity("/api/v1/resources", entity,
			Object.class, "");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		log.info("response >> {}", response.getBody());

	}

	@Test
	@WithMockCustomUser
	void 파일_업로드_실패_파일타입() throws Exception {

		byte[] bytes = new byte[1_024];

		MockMultipartFile file1 = new MockMultipartFile("file", "upload_test.txt",
			MediaType.IMAGE_JPEG_VALUE, bytes);

		mvc.perform(
				multipart("/api/v1/resources")
					.file(file1))
			.andExpect(status().is4xxClientError())
			.andDo(print());

	}

	@Test
	void 파일_업로드_실패_파일사이즈() {

		byte[] bytes = new byte[502 * 1_024];
		MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

		MockMultipartFile file1 = new MockMultipartFile("file", "test.jpeg",
			MediaType.IMAGE_JPEG_VALUE, bytes);

		parameters.add("file", file1.getResource());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);

		ResponseEntity<Object> response = testRestTemplate.postForEntity("/api/v1/resources", entity,
			Object.class, "");

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		log.info("response >> {}", response.getBody());

	}

	@Test
	void 파일_업로드_실패_권한없음() throws Exception {

		byte[] bytes = new byte[1_024];

		MockMultipartFile file1 = new MockMultipartFile("file", "upload_test.jpeg",
			MediaType.IMAGE_JPEG_VALUE, bytes);

		mvc.perform(
				multipart("/api/v1/resources")
					.file(file1))
			.andExpect(status().is4xxClientError())
			.andDo(print());

	}
}
