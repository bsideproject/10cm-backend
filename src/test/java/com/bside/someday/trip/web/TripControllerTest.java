package com.bside.someday.trip.web;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
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
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.bside.someday.security.WithMockCustomUser;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.response.TripDetailResponseDto;
import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;
import com.bside.someday.trip.entity.TripPlace;
import com.bside.someday.trip.repository.TripRepository;
import com.bside.someday.trip.service.TripService;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NonAsciiCharacters")
class TripControllerTest {

	@Autowired
	private TripService tripService;

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private UserRepository userRepository;

	private ObjectMapper objectMapper;

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	DataSource dataSource;

	private User user;

	private AtomicLong atomicLong;

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
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.alwaysDo(print())
			.build();
		this.objectMapper = new ObjectMapper();
		//noinspection OptionalGetWithoutIsPresent
		this.user = userRepository.findById(1L).get();
		this.atomicLong = new AtomicLong(1L);
	}

	@WithMockCustomUser
	@Transactional
	@ParameterizedTest(name = "{index} => requestJson={0}")
	@MethodSource("tripSuccessRequestParams")
	void 여행_등록_성공(String requestJson) throws Exception {

		//given
		TripDetailRequestDto request = objectMapper.readValue(requestJson, TripDetailRequestDto.class);

		//when
		String resultId = mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(MockMvcResultHandlers.print())
			.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

		TripDetailResponseDto result = tripService.getTrip(1L, Long.valueOf(resultId));

		//then
		assertThat(request.getName()).isEqualTo(result.getName());
		assertThat(LocalDate.parse(request.getStartDate())).isEqualTo(result.getStartDate());
		assertThat(LocalDate.parse(request.getEndDate())).isEqualTo(result.getEndDate());
		assertThat(request.getTripImageUrl()).isEqualTo(result.getTripImageUrl());
		assertThat(request.getDescription()).isEqualTo(result.getDescription());
		assertThat("Y".equals(request.getShareYn()) ? "Y" : "N").isEqualTo(result.getShareYn());
		assertThat(request.getTripDetails().size()).isEqualTo(result.getTripDetails().size());
	}

	@WithMockCustomUser
	@Transactional
	@ParameterizedTest(name = "{index} => requestJson={0}")
	@MethodSource("tripFailRequestParams")
	void 여행_등록_실패_파라미터오류(String requestJson) throws Exception {

		mvc.perform(MockMvcRequestBuilders.post("/api/v1/trip")
				.content(requestJson)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_수정_성공() throws Exception {

		//given
		Trip buildTrip = buildTestTrip("N");
		Trip savedTrip = tripRepository.save(buildTrip);

		String requestJSON = "{\n"
			+ "    \"name\": \"여행 제목\",\n"
			+ "    \"description\": \"여행 메모\",\n"
			+ "    \"start_date\": \"2022-01-01\",\n"
			+ "    \"end_date\": \"2022-01-02\",\n"
			+ "    \"share_yn\": \"N\",\n"
			+ "    \"trip_details\":[\n"
			+ "        [\n"
			+ "            {\n"
			+ "                \"name\": \"장소명1-1\",\n"
			+ "                \"address\": \"주소1-1\",\n"
			+ "                \"address_detail\": \"상세주소1-1\",\n"
			+ "                \"phone\": \"02-0000-0000\",\n"
			+ "                \"longitude\": \"126.57102135769145\",\n"
			+ "                \"latitude\": \"33.4507335638693\",\n"
			+ "                \"description\": \"장소설명(옵션)\"\n"
			+ "            }\n"
			+ "        ],\n"
			+ "        [\n"
			+ "             {\n"
			+ "                \"name\": \"장소명2-1\",\n"
			+ "                \"address\": \"주소2-1\",\n"
			+ "                \"address_detail\": \"상세주소2-1\",\n"
			+ "                \"longitude\": \"126.57102135769145\",\n"
			+ "                \"latitude\": \"33.4507335638693\",\n"
			+ "                \"description\": \"장소설명(옵션)\"\n"
			+ "             }\n"
			+ "        ]\n"
			+ "    ]\n"
			+ "}";

		//when
		MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/api/v1/trip/" + savedTrip.getTripId())
				.content(requestJSON)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andReturn();

		TripDetailRequestDto requestDto = objectMapper.readValue(requestJSON, TripDetailRequestDto.class);

		//then
		assertThat(
			tripService.getTripById(Long.parseLong(result.getResponse().getContentAsString())))
			.usingRecursiveComparison()
			.ignoringFieldsOfTypes(LocalDateTime.class, Long.class)
			.isEqualTo(
				Trip.createTrip(requestDto.toEntity(), user,
					tripService.getTripEntryList(requestDto)));
	}

	@Test
	@Transactional
	void 공유된_여행_상세조회_성공() throws Exception {

		Trip testTrip = buildTestTrip("Y");
		Trip savedTrip = tripRepository.save(testTrip);

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser(id = 2L)
	@Transactional
	void 여행_상세조회_실패_권한없음() throws Exception {

		Trip testTrip = buildTestTrip("N");
		Trip savedTrip = tripRepository.save(testTrip);

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());

	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_목록_조회_성공() throws Exception {

		List<Trip> tripList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			tripList.add(tripRepository.save(buildTestTrip("N")));
		}

		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

		//TODO: 페이징(정렬) 테스트 추가

	}

	@Test
	@WithMockCustomUser
	@Transactional
	void 여행_삭제_성공() throws Exception {

		//given
		Trip testTrip = buildTestTrip("Y");
		Trip savedTrip = tripRepository.save(testTrip);

		//when
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

		mvc.perform(MockMvcRequestBuilders.delete("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(MockMvcResultHandlers.print());

		//then
		mvc.perform(MockMvcRequestBuilders.get("/api/v1/trip/" + savedTrip.getTripId()))
			.andExpect(MockMvcResultMatchers.status().is4xxClientError())
			.andDo(MockMvcResultHandlers.print());
	}

	/*
	테스트 여행 빌드
	 */
	private Trip buildTestTrip(String shareYn) {

		long tmpLong = atomicLong.getAndIncrement();

		return Trip.createTrip(Trip.builder()
			.tripName("여행" + tmpLong)
			.description("여행 내용" + tmpLong)
			.startDate(LocalDate.of(2022, 1, 1))
			.endDate(LocalDate.of(2022, 1, 2))
			.shareYn(shareYn)
			.build(), user, List.of(
			TripEntry.createTripEntry(TripEntry.builder()
					.entrySn(1)
					.build(), List.of(
					TripPlace.builder()
						.placeSn(1)
						.name("장소" + tmpLong + " 1-1")
						.description("")
						.address("")
						.addressDetail("")
						.latitude("1.1")
						.longitude("1.1")
						.build()
				)
			),
			TripEntry.createTripEntry(TripEntry.builder()
					.entrySn(2)
					.build(), List.of(
					TripPlace.builder()
						.placeSn(1)
						.name("장소" + tmpLong + " 2-1")
						.description("")
						.address("")
						.addressDetail("")
						.latitude("1.2")
						.longitude("1.2")
						.build(),
					TripPlace.builder()
						.placeSn(2)
						.name("장소" + tmpLong + " 2-2")
						.description("")
						.address("")
						.addressDetail("")
						.latitude("1.3")
						.longitude("1.3")
						.build()
				)
			)
		));
	}

	public static Stream<Arguments> tripSuccessRequestParams() {
		return Stream.of(
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_image_url\": \"http://t.c/t\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"phone\": \"010-0000-0000\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-2\",\n"
					+ "                \"address\": \"주소1-2\",\n"
					+ "                \"address_detail\": \"상세주소1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-1\",\n"
					+ "                \"address\": \"주소2-1\",\n"
					+ "                \"address_detail\": \"상세주소2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-2\",\n"
					+ "                \"address\": \"주소2-2\",\n"
					+ "                \"address_detail\": \"상세주소2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명3-1\",\n"
					+ "                \"address\": \"주소3-1\",\n"
					+ "                \"address_detail\": \"상세주소3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명3-2\",\n"
					+ "                \"address\": \"주소3-2\",\n"
					+ "                \"address_detail\": \"상세주소3-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_image_url\": \"http://t.c/t\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            \n"
					+ "        ],\n"
					+ "        [\n"
					+ "            \n"
					+ "        ],\n"
					+ "        [\n"
					+ "\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_image_url\": \"http://t.c/t\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-1\",\n"
					+ "                \"address\": \"주소2-1\",\n"
					+ "                \"address_detail\": \"상세주소2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-2\",\n"
					+ "                \"address\": \"주소2-2\",\n"
					+ "                \"address_detail\": \"상세주소2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-3\",\n"
					+ "                \"address\": \"주소2-3\",\n"
					+ "                \"address_detail\": \"상세주소2-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명3-1\",\n"
					+ "                \"address\": \"주소3-1\",\n"
					+ "                \"address_detail\": \"상세주소3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-10\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-2\",\n"
					+ "                \"address\": \"주소1-2\",\n"
					+ "                \"address_detail\": \"상세주소1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-3\",\n"
					+ "                \"address\": \"주소1-3\",\n"
					+ "                \"address_detail\": \"상세주소1-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }   \n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-01-02\",\n"
					+ "    \"end_date\": \"2022-01-03\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_image_url\": \"\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-2\",\n"
					+ "                \"address\": \"주소1-2\",\n"
					+ "                \"address_detail\": \"상세주소1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-3\",\n"
					+ "                \"address\": \"주소1-3\",\n"
					+ "                \"address_detail\": \"상세주소1-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }   \n"
					+ "        ],\n"
					+ "        [\n"
					+ "            \n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			)
		);
	}

	public static Stream<Arguments> tripFailRequestParams() {
		return Stream.of(
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"20221210\",\n" // 날짜타입오류(yyyy-MM-dd)
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            \n"
					+ "        ],\n"
					+ "        [\n"
					+ "            \n"
					+ "        ],\n"
					+ "        [\n"
					+ "\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n" // 여행기간 <> entry 개수
					+ "        [\n"
					+ "            \n"
					+ "        ],\n"
					+ "        [\n"
					+ "            \n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					// 필수 값 누락
					//+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-1\",\n"
					+ "                \"address\": \"주소2-1\",\n"
					+ "                \"address_detail\": \"상세주소2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-2\",\n"
					+ "                \"address\": \"주소2-2\",\n"
					+ "                \"address_detail\": \"상세주소2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"장소명2-3\",\n"
					+ "                \"address\": \"주소2-3\",\n"
					+ "                \"address_detail\": \"상세주소2-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"장소명3-1\",\n"
					+ "                \"address\": \"주소3-1\",\n"
					+ "                \"address_detail\": \"상세주소3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-01-02\",\n"
					+ "    \"end_date\": \"2022-01-03\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					// entry 필수 값 누락
					// + "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-01-02\",\n"
					+ "    \"end_date\": \"2022-01-03\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"

					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"longitude\": \"F126.57\",\n" // entry 경도, 위도 형식 오류 (숫자)
					+ "                \"latitude\": \"33.450\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"여행 제목\",\n"
					+ "    \"description\": \"여행 메모\",\n"
					+ "    \"start_date\": \"2022-01-02\",\n"
					+ "    \"end_date\": \"2022-01-03\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"

					+ "                \"name\": \"장소명1-1\",\n"
					+ "                \"address\": \"주소1-1\",\n"
					+ "                \"address_detail\": \"상세주소1-1\",\n"
					+ "                \"phone\": \"0100000000\",\n" // 전화번호(010-0000-0000 형식)
					+ "                \"longitude\": \"126.57\",\n"
					+ "                \"latitude\": \"33.450\",\n"
					+ "                \"description\": \"장소설명(옵션)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			)
		);
	}
}