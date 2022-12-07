package com.bside.someday.trip.web;

import static com.bside.someday.trip.dto.request.TripRequestDto.*;
import static org.springframework.data.domain.Sort.Direction.*;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.someday.common.dto.PageDto;
import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.error.exception.oauth.UnAuthorizedException;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
import com.bside.someday.trip.dto.request.TripRequestDto;
import com.bside.someday.trip.dto.response.TripDetailResponseDto;
import com.bside.someday.trip.dto.response.TripResponseDto;
import com.bside.someday.trip.service.TripService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Trip API")
@Slf4j
@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController {

	private final TripService tripService;

	@ApiOperation("여행 목록 조회")
	@GetMapping
	public ResponseEntity<PageDto<TripResponseDto>> findTrip(@AuthUser UserInfo userInfo,
		@PageableDefault(size = TripRequestDto.DEFAULT_PAGE_SIZE, sort = DEFAULT_SORT_PROPERTY, direction = DESC) Pageable pageable) {
		if (userInfo == null) {
			throw new UnAuthorizedException();
		}
		return PageDto.ok(tripService.searchTrip(userInfo.getUserId(), pageable));
	}

	@ApiOperation("여행 등록")
	@PostMapping
	public ResponseEntity<Long> saveTrip(@AuthUser UserInfo userInfo,
		@Valid @RequestBody TripDetailRequestDto requestDto) {
		if (userInfo == null) {
			throw new UnAuthorizedException();
		}
		return ResponseDto.created(tripService.save(userInfo.getUserId(), requestDto));
	}

	@ApiOperation("여행 상세 조회")
	@GetMapping("/{tripId}")
	public ResponseEntity<TripDetailResponseDto> getTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId) {
		if (userInfo == null) {
			return ResponseDto.ok(tripService.getSharedTrip(tripId));
		}
		return ResponseDto.ok(tripService.getTrip(userInfo.getUserId(), tripId));
	}

	@ApiOperation("여행 상세 수정")
	@PutMapping("/{tripId}")
	public ResponseEntity<Long> updateTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId,
		@Valid @RequestBody TripDetailRequestDto requestDto) {
		if (userInfo == null) {
			throw new UnAuthorizedException();
		}
		return ResponseDto.ok(tripService.update(userInfo.getUserId(), tripId, requestDto));
	}

	@ApiOperation("여행 삭제")
	@DeleteMapping("/{tripId}")
	public ResponseEntity<?> deleteTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId) {
		if (userInfo == null) {
			throw new UnAuthorizedException();
		}
		tripService.delete(userInfo.getUserId(), tripId);
		return ResponseDto.ok(null);
	}

}
