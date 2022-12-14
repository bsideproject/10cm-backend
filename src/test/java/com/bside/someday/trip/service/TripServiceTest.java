package com.bside.someday.trip.service;

import static com.bside.someday.user.dto.SocialType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.request.TripRequestDto;
import com.bside.someday.trip.dto.response.TripDetailResponseDto;
import com.bside.someday.trip.dto.response.TripResponseDto;
import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;
import com.bside.someday.trip.entity.TripPlace;
import com.bside.someday.trip.repository.TripEntryRepository;
import com.bside.someday.trip.repository.TripPlaceRepository;
import com.bside.someday.trip.repository.TripRepository;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
class TripServiceTest {

	private TripService tripService;

	@Mock
	private TripRepository tripRepository;

	@Mock
	private TripEntryRepository tripEntryRepository;

	@Mock
	private TripPlaceRepository tripPlaceRepository;

	@Mock
	private UserService userService;

	private ObjectMapper objectMapper;

	/**
	 * ?????? ????????? ????????? ??????
	 */
	private User createTestUser(Long userId) {
		return User.builder()
			.userId(userId)
			.name("??????" + userId)
			.nickname("?????????" + userId)
			.socialType(KAKAO)
			.socialId("test")
			.build();
	}

	private AtomicLong tripEntryId;
	private AtomicLong tripPlaceId;

	private Trip createTestTrip(Long tripId, String shareYn) {
		return Trip.createTrip(Trip.builder()
			.tripId(tripId)
			.tripName("??????" + tripId)
			.description("?????? ??????" + tripId)
			.startDate(LocalDate.of(2022, 1, 1))
			.endDate(LocalDate.of(2022, 1, 2))
			.shareYn(shareYn)
			.build(), user, List.of(
			TripEntry.createTripEntry(TripEntry.builder()
					.tripEntryId(tripEntryId.getAndIncrement())
					.entrySn(1)
					.build(), List.of(
					TripPlace.builder()
						.tripPlaceId(tripPlaceId.getAndIncrement())
						.placeSn(1)
						.name("??????1")
						.description("")
						.address("")
						.addressDetail("")
						.latitude("1.1")
						.longitude("1.1")
						.build()
				)
			),
			TripEntry.createTripEntry(TripEntry.builder()
					.tripEntryId(tripEntryId.getAndIncrement())
					.entrySn(2)
					.build(), List.of(
					TripPlace.builder()
						.tripPlaceId(tripPlaceId.getAndIncrement())
						.placeSn(1)
						.name("??????2")
						.description("")
						.address("")
						.addressDetail("")
						.latitude("1.2")
						.longitude("1.2")
						.build(),
					TripPlace.builder()
						.tripPlaceId(tripPlaceId.getAndIncrement())
						.placeSn(2)
						.name("??????3")
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

	private User user;

	@BeforeEach
	void setup() {
		this.tripService = new TripService(tripRepository, tripEntryRepository, tripPlaceRepository, userService);

		this.objectMapper = new ObjectMapper();
		this.user = createTestUser(1L);
		this.tripEntryId = new AtomicLong(1L);
		this.tripPlaceId = new AtomicLong(1L);
	}

	@ParameterizedTest(name = "{index} => requestJson={0}")
	@MethodSource("tripSuccessRequestParams")
	void ??????_??????_??????(String jsonString) throws JsonProcessingException {

		//given
		TripDetailRequestDto requestDto = objectMapper.readValue(jsonString, TripDetailRequestDto.class);

		when(userService.findOneById(anyLong())).thenReturn(user);
		when(tripRepository.save(any())).thenReturn(Trip.builder().tripId(1L).build());

		//when
		Long tripId = tripService.save(1L, requestDto);

		ArgumentCaptor<Trip> captor = ArgumentCaptor.forClass(Trip.class);
		verify(tripRepository).save(captor.capture());

		//then
		assertThat(tripId).isEqualTo(1L);
		assertThat(captor.getValue().getTripName()).isEqualTo(requestDto.getName());
		assertThat(captor.getValue().getDescription()).isEqualTo(requestDto.getDescription());

	}

	@ParameterizedTest(name = "{index} => requestJson={0}")
	@MethodSource("tripFailRequestParams")
	void ??????_??????_??????(String jsonString) throws JsonProcessingException {

		//given
		TripDetailRequestDto requestDto = objectMapper.readValue(jsonString, TripDetailRequestDto.class);

		//when
		when(userService.findOneById(anyLong())).thenReturn(user);

		//then
		assertThrows(RuntimeException.class, () -> tripService.save(1L, requestDto));

	}


	@Test
	void ??????_????????????_??????() {

		//given
		PageRequest request = TripRequestDto.of(0, 10);
		List<Trip> tripList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tripList.add(createTestTrip((long)i, "N"));
		}

		Page<Trip> tripPage = new PageImpl<>(tripList, PageRequest.of(request.getPageNumber(), request.getPageSize()), tripList.size());
		when(tripRepository.findAllByUserId(user.getUserId(), request)).thenReturn(
			new PageImpl<>(tripPage.stream().limit(request.getPageSize()).collect(Collectors.toList())));

		//when
		List<TripResponseDto> response = tripService.searchTrip(user.getUserId(), request).getContent();

		//then
		assertThat(response.size()).isEqualTo(request.getPageSize());
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(tripList.stream()
				.limit(request.getPageSize())
				.map(TripResponseDto::new)
				.collect(Collectors.toList()));
	}


	@Test
	void ??????_????????????_??????() {

		//given
		Trip trip = createTestTrip(1L, "N");

		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user.getUserId())).thenReturn(user);
		when(tripEntryRepository.findAllByTrip(trip)).thenReturn(trip.getTripEntryList());

		//when
		TripDetailResponseDto response = tripService.getTrip(user.getUserId(), trip.getTripId());

		//then
		assertThat(response).usingRecursiveComparison().isEqualTo(new TripDetailResponseDto(trip, trip.getTripEntryList()));
		log.info(response.toString());
	}

	@Test
	void ??????_????????????_??????_????????????() {

		//given
		User user2 = createTestUser(2L);
		Trip trip = createTestTrip(1L, "N");

		//when
		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user2.getUserId())).thenReturn(user2);
		when(tripEntryRepository.findAllByTrip(trip)).thenReturn(trip.getTripEntryList());

		//then
		assertThrows(NotAllowAccessException.class,
			() -> tripService.getTrip(user2.getUserId(), trip.getTripId()));
	}


	@Test
	void ?????????_??????_????????????_??????() {

		//given
		User user2 = createTestUser(2L);
		Trip trip = createTestTrip(1L, "Y");

		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user2.getUserId())).thenReturn(user2);
		when(tripEntryRepository.findAllByTrip(trip)).thenReturn(trip.getTripEntryList());

		//when
		TripDetailResponseDto response = tripService.getTrip(user2.getUserId(), trip.getTripId());

		//then
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(new TripDetailResponseDto(trip, trip.getTripEntryList()));
	}

	public static Stream<Arguments> tripSuccessRequestParams() {
		return Stream.of(
			Arguments.of(
				"{\n"
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"trip_image_url\": \"http://c.t.c/t\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"id\": \"12341324\",\n"
					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"phone\": \"010-0000-0000\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"id\": \"32323\",\n"
					+ "                \"name\": \"?????????1-2\",\n"
					+ "                \"address\": \"??????1-2\",\n"
					+ "                \"address_detail\": \"????????????1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"id\": \"22222\",\n"
					+ "                \"name\": \"?????????2-1\",\n"
					+ "                \"address\": \"??????2-1\",\n"
					+ "                \"address_detail\": \"????????????2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"id\": \"121212\",\n"
					+ "                \"name\": \"?????????2-2\",\n"
					+ "                \"address\": \"??????2-2\",\n"
					+ "                \"address_detail\": \"????????????2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"id\": \"22212122\",\n"
					+ "                \"name\": \"?????????3-1\",\n"
					+ "                \"address\": \"??????3-1\",\n"
					+ "                \"address_detail\": \"????????????3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"id\": \"22212123\",\n"
					+ "                \"name\": \"?????????3-2\",\n"
					+ "                \"address\": \"??????3-2\",\n"
					+ "                \"address_detail\": \"????????????3-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
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
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-1\",\n"
					+ "                \"address\": \"??????2-1\",\n"
					+ "                \"address_detail\": \"????????????2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-2\",\n"
					+ "                \"address\": \"??????2-2\",\n"
					+ "                \"address_detail\": \"????????????2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-3\",\n"
					+ "                \"address\": \"??????2-3\",\n"
					+ "                \"address_detail\": \"????????????2-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????3-1\",\n"
					+ "                \"address\": \"??????3-1\",\n"
					+ "                \"address_detail\": \"????????????3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-10\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-2\",\n"
					+ "                \"address\": \"??????1-2\",\n"
					+ "                \"address_detail\": \"????????????1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-3\",\n"
					+ "                \"address\": \"??????1-3\",\n"
					+ "                \"address_detail\": \"????????????1-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }   \n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-01-02\",\n"
					+ "    \"end_date\": \"2022-01-03\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-2\",\n"
					+ "                \"address\": \"??????1-2\",\n"
					+ "                \"address_detail\": \"????????????1-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-3\",\n"
					+ "                \"address\": \"??????1-3\",\n"
					+ "                \"address_detail\": \"????????????1-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
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
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"20221210\",\n" // ??????????????????(yyyy-MM-dd)
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
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n" // ???????????? <> entry ??????
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
					// ?????? ??? ??????
					//+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2022-12-10\",\n"
					+ "    \"end_date\": \"2022-12-12\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-1\",\n"
					+ "                \"address\": \"??????2-1\",\n"
					+ "                \"address_detail\": \"????????????2-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-2\",\n"
					+ "                \"address\": \"??????2-2\",\n"
					+ "                \"address_detail\": \"????????????2-2\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            },\n"
					+ "            {\n"
					+ "                \"name\": \"?????????2-3\",\n"
					+ "                \"address\": \"??????2-3\",\n"
					+ "                \"address_detail\": \"????????????2-3\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ],\n"
					+ "        [\n"
					+ "            {\n"
					+ "                \"name\": \"?????????3-1\",\n"
					+ "                \"address\": \"??????3-1\",\n"
					+ "                \"address_detail\": \"????????????3-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
					+ "            }\n"
					+ "        ]\n"
					+ "    ]\n"
					+ "}"
			),
			Arguments.of(
				"{\n"
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2023-1-2\",\n"
					+ "    \"end_date\": \"2022-1-3\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"
					// entry ?????? ??? ??????
					// + "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"126.57102135769145\",\n"
					+ "                \"latitude\": \"33.4507335638693\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
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
					+ "    \"name\": \"?????? ??????\",\n"
					+ "    \"description\": \"?????? ??????\",\n"
					+ "    \"start_date\": \"2023-1-2\",\n"
					+ "    \"end_date\": \"2022-1-3\",\n"
					+ "    \"share_yn\": \"N\",\n"
					+ "    \"trip_details\":[\n"
					+ "        [\n"
					+ "            {\n"

					+ "                \"name\": \"?????????1-1\",\n"
					+ "                \"address\": \"??????1-1\",\n"
					+ "                \"address_detail\": \"????????????1-1\",\n"
					+ "                \"longitude\": \"F126.57\",\n" // entry ??????, ?????? ?????? ?????? (??????)
					+ "                \"latitude\": \"33.450\",\n"
					+ "                \"description\": \"????????????(??????)\"\n"
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