package com.bside.someday.trip.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.trip.TripInvalidParameterException;
import com.bside.someday.error.exception.trip.TripNotFoundException;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.request.TripDetails;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripService {

	private final TripRepository tripRepository;
	private final TripEntryRepository tripEntryRepository;

	private final TripPlaceRepository tripPlaceRepository;
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

		if (!trip.getUser().getUserId().equals(user.getUserId())) {
			throw new NotAllowAccessException();
		}

		// 수정 전 방문 장소 목록 삭제
		trip.getTripEntryList().forEach(tripEntry -> tripPlaceRepository.deleteAll(tripEntry.getTripPlaceList()));
		tripEntryRepository.deleteAll(trip.getTripEntryList());

		return tripRepository.save(Trip.updateTrip(tripId, requestDto.toEntity(), user, getTripEntryList(requestDto)))
			.getTripId();
	}

	@Transactional
	public List<TripEntry> getTripEntryList(TripDetailRequestDto requestDto) {

		List<List<TripDetails>> lists = requestDto.getTripDetails();
		List<TripEntry> tripEntryList = new ArrayList<>();

		for (int i = 0; i < lists.size(); i++) {

			List<TripPlace> tripPlaceList = new ArrayList<>();
			List<TripDetails> tripDetailsList = lists.get(i);

			for (int j = 0; j < tripDetailsList.size(); j++) {
				tripPlaceList.add(tripDetailsList.get(j).toEntity(j + 1));
			}
			tripEntryList.add(TripEntry.createTripEntry(TripEntry.builder().entrySn(i + 1).build(), tripPlaceList));
		}

		return tripEntryList;
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

		if (!"Y".equals(trip.getShareYn()) && !user.getUserId().equals(trip.getUser().getUserId())) {
			throw new NotAllowAccessException();
		}

		return new TripDetailResponseDto(trip, tripEntryRepository.findAllByTrip(trip));
	}

	/**
	 * 여행 공유된 여행 조회
	 * @param tripId 조회할 여행 아이디
	 * @return TripDetailResponseDto
	 */
	@Transactional
	public TripDetailResponseDto getSharedTrip(Long tripId) {

		Trip trip = getTripById(tripId);

		if ("N".equals(trip.getShareYn())) {
			throw new NotAllowAccessException();
		}

		return new TripDetailResponseDto(trip, tripEntryRepository.findAllByTrip(trip));
	}


	/**
	 * 여행 목록 검색
	 * @param userId 조회할 사용자 아이디
	 * @param pageable 요청 페이징 정보
	 * @return List<TripResponseDto>
	 */
	@Transactional
	public Page<TripResponseDto> searchTrip(Long userId, Pageable pageable) {

		if (pageable == null) {
			throw new TripInvalidParameterException();
		}

		int page = pageable.getPageNumber() == 0 ? 0 : pageable.getPageNumber() - 1;

		Page<Trip> tripPage = tripRepository.findAllByUserId(userId,
			PageRequest.of(page, pageable.getPageSize(), pageable.getSort()));

		return tripPage.map(TripResponseDto::new);
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

		trip.getTripEntryList().forEach(tripEntry -> tripPlaceRepository.deleteAll(tripEntry.getTripPlaceList()));
		tripEntryRepository.deleteAll(trip.getTripEntryList());
		tripRepository.delete(trip);
	}


}