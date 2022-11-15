package com.bside.someday.trip.web;

import java.util.List;

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

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.trip.dto.request.TripDetailRequestDto;
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
	public ResponseEntity<?> findTrip(@AuthUser UserInfo userInfo,
		@PageableDefault(size = 12) Pageable pageable) {

		List<TripResponseDto> responseDto = tripService.searchTrip(userInfo.getUserId(),
			pageable);

		return ResponseDto.ok(responseDto);
	}

	@ApiOperation("여행 등록")
	@PostMapping
	public ResponseEntity<?> saveTrip(@AuthUser UserInfo userInfo, @RequestBody TripDetailRequestDto requestDto) {
		return ResponseDto.created(tripService.save(userInfo.getUserId(), requestDto));
	}

	@ApiOperation("여행 상세 조회")
	@GetMapping("/{tripId}")
	public ResponseEntity<?> getTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId) {
		return ResponseDto.ok(tripService.getTrip(userInfo.getUserId(), tripId));
	}

	@ApiOperation("여행 상세 수정")
	@PutMapping("/{tripId}")
	public ResponseEntity<?> updateTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId,
		@RequestBody TripDetailRequestDto requestDto) {
		return ResponseDto.ok(tripService.update(userInfo.getUserId(), tripId, requestDto));
	}

	@ApiOperation("여행 삭제")
	@DeleteMapping("/{tripId}")
	public ResponseEntity<?> deleteTrip(@AuthUser UserInfo userInfo, @PathVariable Long tripId) {
		tripService.delete(userInfo.getUserId(), tripId);
		return ResponseDto.ok(null);
	}

}