package com.bside.someday.trip.service;

import static com.bside.someday.user.dto.SocialType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.trip.TripInvalidParameterException;
import com.bside.someday.error.exception.trip.TripPlaceNotFoundException;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.request.TripPlaceRequestDto;
import com.bside.someday.trip.dto.request.TripRequestDto;
import com.bside.someday.trip.dto.response.TripDetailResponseDto;
import com.bside.someday.trip.dto.response.TripResponseDto;
import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;
import com.bside.someday.trip.repository.TripDetailRepository;
import com.bside.someday.trip.repository.TripRepository;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TripServiceTest {

	private TripService tripService;

	@Mock
	private TripRepository tripRepository;

	@Mock
	private TripDetailRepository tripDetailRepository;

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private UserService userService;



	/**
	 * 유저 테스트 데이터 생성
	 */
	private User createTestUser(Long userId) {
		return User.builder()
			.userId(userId)
			.name("테스트")
			.socialType(KAKAO)
			.socialId("test")
			.build();
	}

	private long nextPlaceId = 1L;

	/**
	 * 장소 테스트 데이터 생성
	 */
	private Place createTestPlace() {
		return Place.builder()
			.id(nextPlaceId++)
			.name("장소테스트1")
			.address("장소상세1")
			.addressDetail("주소상세1")
			.latitude("1")
			.longitude("1")
			.build();
	}

	/**
	 * 여행 테스트 데이터 생성
	 */
	private Trip createTestTrip(Long tripId, User user) {
		List<TripEntry> tripEntries = new ArrayList<>();

		for (int i = 0; i < 5; i++) {

			tripEntries.add(
				TripEntry.builder()
					.place(createTestPlace())
					.visitDate(LocalDate.of(2022, 10, 28))
					.build());
		}

		Trip trip = Trip.builder()
			.tripId(tripId)
			.tripName("여행 제목1")
			.description("여행 내용1")
			.startDate(LocalDate.of(2022, 10, 28))
			.endDate(LocalDate.of(2022, 10, 29))
			.shareYn("N")
			.build();

		return Trip.createTrip(trip, user, tripEntries);
	}

	/**
	 * 여행 테스트 데이터 생성
	 */
	private Trip createTestSharedTrip(Long tripId, User user) {
		List<TripEntry> tripEntries = new ArrayList<>();

		for (int i = 0; i < 5; i++) {

			tripEntries.add(
				TripEntry.builder()
					.place(createTestPlace())
					.placeSn(i + 1)
					.visitDate(LocalDate.of(2022, 10, 28))
					.build());
		}

		Trip trip = Trip.builder()
			.tripId(tripId)
			.tripName("여행 제목1")
			.description("여행 내용1")
			.startDate(LocalDate.of(2022, 10, 28))
			.endDate(LocalDate.of(2022, 10, 29))
			.shareYn("Y")
			.build();

		return Trip.createTrip(trip, user, tripEntries);
	}

	private User user;

	@BeforeEach
	void setup() {
		MockitoAnnotations.initMocks(this);

		tripService = new TripService(tripRepository, tripDetailRepository, placeRepository, userService);

		user = createTestUser(1L);
	}

	@Test
	void 여행_등록_성공() {

		//given
		Place place = createTestPlace();

		List<TripPlaceRequestDto> placeList = new ArrayList<>();
		placeList.add(new TripPlaceRequestDto(place.getId(), 1, "2022-10-28"));
		TripDetailRequestDto request = new TripDetailRequestDto(1L, "여행1", "여행메모", "2022-10-28", "2022-10-29", "N",
			placeList);

		when(userService.findOneById(user.getUserId())).thenReturn(user);
		when(tripRepository.save(any())).thenReturn(request.toEntity());
		when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

		//when
		Long tripId = tripService.save(user.getUserId(), request);

		//then
		assertThat(tripId).isEqualTo(1L);
	}

	@Test
	void 여행_등록_실패_장소누락() {

		//given
		Place place = createTestPlace();
		Long noPlaceId = 999L;
		List<TripPlaceRequestDto> placeList = new ArrayList<>();
		placeList.add(new TripPlaceRequestDto(place.getId(), 1, "2022-10-28"));
		placeList.add(new TripPlaceRequestDto(noPlaceId, 2, "2022-10-28"));
		TripDetailRequestDto request = new TripDetailRequestDto(1L, "여행1", "여행메모", "2022-10-28", "2022-10-29", "N",
			placeList);

		//when
		when(userService.findOneById(user.getUserId())).thenReturn(user);
		when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));
		when(placeRepository.findById(noPlaceId)).thenReturn(Optional.empty());

		//then
		assertThrows(TripPlaceNotFoundException.class,
			() -> tripService.save(user.getUserId(), request));

	}

	@Test
	void 여행_등록_실패_시작종료일() {

		//given
		Place place = createTestPlace();
		List<TripPlaceRequestDto> placeList = new ArrayList<>();
		placeList.add(new TripPlaceRequestDto(place.getId(), 1, "2022-10-28"));
		TripDetailRequestDto request = new TripDetailRequestDto(1L, "여행1", "여행메모", "2022-10-29", "2022-10-28", "N",
			placeList);

		//when
		when(userService.findOneById(user.getUserId())).thenReturn(user);

		//then
		assertThrows(TripInvalidParameterException.class,
			() -> tripService.save(user.getUserId(), request));

	}

	@Test
	void 여행_목록조회_성공() {

		//given
		PageRequest request = TripRequestDto.of(1, 10);
		List<Trip> tripList = new ArrayList<>();
		for (int i = 0; i < 15; i++) {
			tripList.add(createTestTrip((long)i, user));
		}
		Page<Trip> tripPage = new PageImpl<>(tripList, PageRequest.of(request.getPageNumber(), request.getPageSize()), tripList.size());
		when(tripRepository.findAllByUserId(user.getUserId(), request)).thenReturn(
			new PageImpl<>(tripPage.stream().limit(request.getPageSize()).collect(Collectors.toList())));

		//when
		List<TripResponseDto> response = tripService.searchTrip(user.getUserId(), request);

		//then
		assertThat(response.size()).isEqualTo(request.getPageSize());
		assertThat(response).usingRecursiveComparison()
			.isEqualTo(tripList.stream().limit(request.getPageSize()).collect(Collectors.toList()));
	}

	@Test
	void 여행_상세조회_성공() {

		//given
		Trip trip = createTestTrip(1L, user);

		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user.getUserId())).thenReturn(user);

		//when
		TripDetailResponseDto response = tripService.getTrip(user.getUserId(), trip.getTripId());

		//then
		assertThat(response).usingRecursiveComparison().isEqualTo(new TripDetailResponseDto(trip));

	}

	@Test
	void 여행_상세조회_실패_권한없음() {

		//given
		User user2 = createTestUser(2L);
		Trip trip = createTestTrip(1L, user);
		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user2.getUserId())).thenReturn(user2);

		//when
		final RuntimeException exception = assertThrows(RuntimeException.class,
			() -> tripService.getTrip(user2.getUserId(), trip.getTripId()));

		//then
		assertThat(exception).isInstanceOf(NotAllowAccessException.class);
	}


	@Test
	void 여행_공유_상세조회_성공() {

		//given
		User user2 = createTestUser(2L);
		Trip trip = createTestSharedTrip(1L, user);

		when(tripRepository.findById(trip.getTripId())).thenReturn(Optional.of(trip));
		when(userService.findOneById(user.getUserId())).thenReturn(user);

		//when
		TripDetailResponseDto response = tripService.getTrip(user.getUserId(), trip.getTripId());

		//then
		assertThat(response).usingRecursiveComparison().isEqualTo(new TripDetailResponseDto(trip));
	}

}