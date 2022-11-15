package com.bside.someday.trip.web;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.util.ArrayList;
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

	private List<Place> placeList = new ArrayList<>();

	@BeforeEach
	void setUp() {
		User user = userRepository.save(User.builder()
			.name("테스트1")
			.email("test1@test.com")
			.build());

		userRepository.save(User.builder()
			.name("테스트2")
			.email("test2@test.com")
			.build());

		for (int i = 0; i < 2; i++) {
			placeList.add(placeRepository.save(Place.builder()
				.name("장소" + i)
				.description("메모")
				.phone("010")
				.address("주소")
				.addressDetail("주소상세")
				.latitude("1.1")
				.longitude("1.1")
				.user(user)
				.build()));
		}
	}

	long saveTrip() {

		//여행 생성
		TripDetailRequestDto requestDto = new TripDetailRequestDto(1L, "여행", "메모", "2022-11-11",
			"2022-11-11", "N",
			List.of(
				new TripPlaceRequestDto(
					placeList.get(0).getId(), 1, "2022-11-11"),
				new TripPlaceRequestDto(
					placeList.get(1).getId(), 2, "2022-11-11")));
		
		return tripService.save(1L, requestDto);
	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_등록_성공() throws Exception {

		String requestJson1 = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			+ "      \"placeId\": \"1\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"placeId\": \"2\",\n"
			+ "      \"placeSn\": 2,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"shareYn\": \"N\",\n"
			+ "  \"startDate\": \"2022-10-29\",\n"
			+ "  \"tripName\": \"asdasdsad\"\n"
			+ "}";

		String requestJson2 = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"shareYn\": \"N\",\n"
			+ "  \"startDate\": \"2022-10-29\",\n"
			+ "  \"tripName\": \"asdasdfddsad\",\n"
			+ "  \"placeList\": []\n"
			+ "}";

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(MockMvcResultHandlers.print());

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/1"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson2)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(MockMvcResultHandlers.print());

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/2"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_등록_실패_날짜타입오류() throws Exception {

		String requestJson = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			+ "      \"placeId\": \"5\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"shareYn\": \"N\",\n"
			+ "  \"startDate\": \"20221029\",\n" // 날짜 타입 오류 yyyy-MM-dd
			+ "  \"tripName\": \"asdasdsad\"\n"
			+ "}";

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_등록_실패_필수값누락() throws Exception {

		String requestJson = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			// + "      \"placeId\": \"5\",\n"
			+ "      \"placeSn\": 1,\n"
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
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());
	}

}