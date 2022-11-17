package com.bside.someday.trip.web;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.bside.someday.place.entity.Place;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.security.WithMockCustomUser;
import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;
import com.bside.someday.trip.repository.TripRepository;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NonAsciiCharacters")
class TripControllerTest {

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc mvc;

	private List<Place> placeList = new ArrayList<>();
	private User user = null;

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
	void setUp() {
		user = userRepository.findById(1L).get();
		placeList = placeRepository.findAllByUser_UserId(PageRequest.of(0, 3), user.getUserId()).getContent();
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
			+ "      \"placeId\": \"" + placeList.get(0).getId() + "\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"placeId\": \"" + placeList.get(1).getId() + "\",\n"
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

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson2)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@Transactional
	void 여행_등록_실패_권한없음() throws Exception {

		String requestJson1 = "{\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			+ "      \"placeId\": \"" + placeList.get(0).getId() + "\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"placeId\": \"" + placeList.get(1).getId() + "\",\n"
			+ "      \"placeSn\": 2,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"shareYn\": \"N\",\n"
			+ "  \"startDate\": \"2022-10-29\",\n"
			+ "  \"tripName\": \"asdasdsad\"\n"
			+ "}";

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson1)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	@WithMockCustomUser
	@Transactional
	@ParameterizedTest(name = "{index} => requestJson={0}")
	@MethodSource("jsonRequestParams")
	void 여행_등록_실패_파라미터오류(String requestJson) throws Exception {

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	private static Stream<Arguments> jsonRequestParams() {
		return Stream.of(
			Arguments.of(
				"{\n"
					+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
					+ "  \"endDate\": \"2022-10-29\",\n"
					+ "  \"placeList\": [\n"
					+ "    {\n"
					+ "      \"placeId\": \"1\",\n"
					+ "      \"placeSn\": 1,\n"
					+ "      \"visitDate\": \"2022-10-29\"\n"
					+ "    }\n"
					+ "  ],\n"
					+ "  \"shareYn\": \"N\",\n"
					+ "  \"startDate\": \"20221029\",\n" // 날짜 타입 오류 yyyy-MM-dd
					+ "  \"tripName\": \"asdasdsad\"\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
					+ "  \"endDate\": \"2022-10-29\",\n"
					+ "  \"placeList\": [\n"
					+ "    {\n"
					+ "      \"placeId\": \"1\",\n"
					+ "      \"placeSn\": 1,\n"
					+ "      \"visitDate\": \"2022-10-29\"\n"
					+ "    }\n"
					+ "  ],\n"
					+ "  \"shareYn\": \"D\",\n" // shareYn [Y|N]
					 + "  \"startDate\": \"2022-10-29\",\n"
					+ "  \"tripName\": \"asdasdsad\"\n"
					+ "}"
			),
			Arguments.of("{\n"
				+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
				+ "  \"endDate\": \"2022-10-29\",\n"
				+ "  \"placeList\": [\n"
				+ "    {\n"
				// placeId 누락
				+ "      \"placeSn\": 1,\n"
				+ "      \"visitDate\": \"2022-10-29\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"shareYn\": \"N\",\n"
				+ "  \"startDate\": \"2022-10-29\",\n"
				+ "  \"tripName\": \"asdasdsad\"\n"
				+ "}")
		);
	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_수정_성공() throws Exception {

		Trip savedTrip = saveTestTrip("N");

		String requestJson = "{\n"
			+ "  \"tripId\":" + savedTrip.getTripId() + ",\n"
			+ "  \"description\": \"ㅁㄴㅇㄹ\",\n"
			+ "  \"endDate\": \"2022-10-29\",\n"
			+ "  \"placeList\": [\n"
			+ "    {\n"
			+ "      \"placeId\": \"1\",\n"
			+ "      \"placeSn\": 1,\n"
			+ "      \"visitDate\": \"2022-10-29\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"shareYn\": \"Y\",\n"
			+ "  \"startDate\": \"2022-10-29\",\n"
			+ "  \"tripName\": \"asdasdsad\"\n"
			+ "}";

		mvc.perform(MockMvcRequestBuilders.put("/api/v1/trip/" + savedTrip.getTripId())
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@Transactional
	void 공유된_여행_상세조회_성공() throws Exception {

		Trip savedTrip = saveTestTrip("Y");

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser(id = 2L)
	@Transactional
	void 여행_상세조회_실패_권한없음() throws Exception {

		Trip savedTrip = saveTestTrip("N");

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	private Trip saveTestTrip(String shareYn) {

		Trip trip = Trip.builder()
			.tripName("여행 제목1")
			.description("여행 내용1")
			.startDate(LocalDate.of(2022, 10, 28))
			.endDate(LocalDate.of(2022, 10, 28))
			.shareYn(shareYn)
			.build();

		List<TripEntry> tripEntries = new ArrayList<>();
		for (int i = 0; i <= 1; i++) {
			tripEntries.add(
				TripEntry.builder()
					.place(placeRepository.findById(placeList.get(i).getId()).get())
					.visitDate(LocalDate.of(2022, 10, 28))
					.placeSn(i + 1)
					.build());
		}

		return tripRepository.save(Trip.createTrip(trip, user, tripEntries));
	}
}