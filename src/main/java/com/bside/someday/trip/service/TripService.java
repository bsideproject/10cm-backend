package com.bside.someday.trip.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.trip.TripNotFoundException;
import com.bside.someday.error.exception.trip.TripPlaceNotFoundException;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.response.TripDetailResponseDto;
import com.bside.someday.trip.dto.response.TripResponseDto;
import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;
import com.bside.someday.trip.repository.TripDetailRepository;
import com.bside.someday.trip.repository.TripRepository;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {

	private final TripRepository tripRepository;
	private final TripDetailRepository tripDetailRepository;
	private final PlaceRepository placeRepository;
	private final UserService userService;


	/**
	 * 여행 단건 조회
	 * @param tripId 조회할 여행 아이디
	 * @return trip
	 */
	@Transactional
	public Trip getTripById(Long tripId) {
		return tripRepository.findById(tripId).orElseThrow(TripNotFoundException::new);
	}

	/**
	 * 여행 등록
	 * @param userId 사용자 아이디
	 * @param requestDto 등록 요청 request
	 * @return tripId
	 */
	@Transactional
	public Long save(Long userId, TripDetailRequestDto requestDto) {
		User user = userService.findOneById(userId);

		return tripRepository.save(Trip.createTrip(requestDto.toEntity(), user, getTripEntryList(requestDto)))
			.getTripId();
	}

	/***
	 * 여행 수정
	 * @param userId 사용자 아이디
	 * @param tripId 수정할 여행 아이디
	 * @param requestDto 수정 요청 request
	 * @return tripId
	 */
	@Transactional
	public Long update(Long userId, Long tripId, TripDetailRequestDto requestDto) {

		User user = userService.findOneById(userId);
		Trip trip = getTripById(tripId);

		if (!trip.getTripId().equals(requestDto.getTripId())) {
			throw new TripNotFoundException();
		}

		if (!trip.getCreatedBy().equals(user.getUserId())) {
			throw new NotAllowAccessException();
		}

		// 수정 전 방문 장소 목록 삭제
		tripDetailRepository.deleteAll(trip.getTripEntryList());

		return tripRepository.save(Trip.createTrip(requestDto.toEntity(), user, getTripEntryList(requestDto)))
			.getTripId();
	}

	@Transactional
	public List<TripEntry> getTripEntryList(TripDetailRequestDto requestDto) {

		// 22.11.15 - 저장된 장소만 입력 가능하도록 변경
		/*
		List<TripEntry> entryList = new ArrayList<>();

		for (TripPlaceRequestDto tripPlaceRequestDto : requestDto.getPlaceList()) {
			Long placeId = tripPlaceRequestDto.getPlaceId();
			Place place;
			if (placeId != null) {
				place = placeRepository.findById(placeId).orElseThrow(TripPlaceNotFoundException::new);
			} else {
				place = placeRepository.save(tripPlaceRequestDto.toEntity());
			}
			entryList.add(tripPlaceRequestDto.toTripEntity(place));
		}
		return entryList;
		*/
		return requestDto.getPlaceList().stream()
			.map(tripPlaceRequestDto -> tripPlaceRequestDto.toTripEntity(
				placeRepository.findById(tripPlaceRequestDto.getPlaceId())
					.orElseThrow(TripPlaceNotFoundException::new)))
			.collect(Collectors.toList());
	}

	/**
	 * 여행 상세정보 조회
	 * @param userId 조회할 사용자 아이디
	 * @param tripId 조회할 여행 아이디
	 * @return TripDetailResponseDto
	 */
	@Transactional
	public TripDetailResponseDto getTrip(Long userId, Long tripId) {

		Trip trip = getTripById(tripId);
		User user = userService.findOneById(userId);

		if ("N".equals(trip.getShareYn()) && !user.getUserId().equals(trip.getUser().getUserId())) {
			throw new NotAllowAccessException();
		}

		return new TripDetailResponseDto(trip, tripDetailRepository.findAllByTrip(trip));
	}

	/**
	 * 여행 목록 검색
	 * @param userId 조회할 사용자 아이디
	 * @param pageable 요청 페이징 정보
	 * @return List<TripResponseDto>
	 */
	@Transactional
	public List<TripResponseDto> searchTrip(Long userId, Pageable pageable) {

		Page<Trip> tripPage = tripRepository.findAllByUserId(userId, pageable);

		return tripPage.map(TripResponseDto::new).getContent();
	}

	/**
	 * 여행 삭제
	 * @param userId 사용자 아이디
	 * @param tripId 삭제할 여행 아이디
	 */
	@Transactional
	public void delete(Long userId, Long tripId) {

		Trip trip = getTripById(tripId);
		User user = userService.findOneById(userId);
		if ("N".equals(trip.getShareYn()) && !user.getUserId().equals(trip.getUser().getUserId())) {
			throw new NotAllowAccessException();
		}

		tripDetailRepository.deleteAll(trip.getTripEntryList());
	}


}