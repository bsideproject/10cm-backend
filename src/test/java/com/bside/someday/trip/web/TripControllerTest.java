package com.bside.someday.trip.web;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.bside.someday.place.entity.Place;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.security.WithMockCustomUser;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.request.TripPlaceRequestDto;
import com.bside.someday.trip.service.TripService;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class TripControllerTest {

	@Autowired
	private TripService tripService;
	@Autowired
	private PlaceRepository placeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		userRepository.save(User.builder()
			.userId(1L)
			.name("테스트")
			.email("test@test.com")
			.build()).getUserId();

	}

	long saveTrip() {

		//여행 생성
		TripDetailRequestDto requestDto = new TripDetailRequestDto(1L, "여행", "메모", "2022-11-11",
			"2022-11-11", "N",
			List.of(
				new TripPlaceRequestDto(
					placeRepository.save(
						Place.builder()
							.name("장소1")
							.address("주소")
							.addressDetail("주소상세")
							.image("이미지경로")
							.memo("메모")
							.longitude("11.1")
							.latitude("1.1")
							.build()).getId(), 1, "2022-11-11"),
				new TripPlaceRequestDto(
					placeRepository.save(
						Place.builder()
							.name("장소2")
							.address("주소")
							.addressDetail("주소상세")
							.image("이미지경로")
							.memo("메모")
							.longitude("11.1")
							.latitude("1.1")
							.build()).getId(), 2, "2022-11-11")));

		return tripService.save(1L, requestDto);
	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_등록_성공() throws Exception {

		String requestJson = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			+ "      \"address\": \"string\",\n"
			+ "      \"addressDetail\": \"string\",\n"
			+ "      \"image\": \"\",\n"
			+ "      \"latitude\": \"12\",\n"
			+ "      \"longitude\": \"121\",\n"
			+ "      \"memo\": \"ㅁㄴㅇㅁㄴㅇ\",\n"
			+ "      \"name\": \"ㅁㄴㅇㅁㄴㅇ\",\n"
			+ "      \"phone\": \"1212\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"tag\": [\n"
			+ "        \"string\"\n"
			+ "      ],\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"address\": \"string\",\n"
			+ "      \"addressDetail\": \"string\",\n"
			+ "      \"image\": \"\",\n"
			+ "      \"latitude\": \"12\",\n"
			+ "      \"longitude\": \"121\",\n"
			+ "      \"memo\": \"ㅁㄴㅇqeqㅁㄴㅇ\",\n"
			+ "      \"name\": \"ㅁㄴㅇqeㅁㄴㅇ\",\n"
			+ "      \"phone\": \"1212\",\n"
			+ "      \"placeSn\": 2,\n"
			+ "      \"tag\": [\n"
			+ "        \"string\"\n"
			+ "      ],\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"shareYn\": \"N\",\n"
			+ "  \"startDate\": \"2022-10-29\",\n"
			+ "  \"tripName\": \"asdasdsad\"\n"
			+ "}";

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(MockMvcResultHandlers.print());


		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

	}

}