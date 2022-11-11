package com.bside.someday.trip.dto.response;

import java.time.LocalDate;

import com.bside.someday.trip.entity.Trip;

import lombok.Getter;

@Getter
public class TripResponseDto {

	private final Long tripId;

	private final String tripName;

	private final String description;

	private final LocalDate startDate;

	private final LocalDate endDate;

	private final String shareYn;

	public TripResponseDto(Trip trip) {
		this.tripId = trip.getTripId();
		this.tripName = trip.getTripName();
		this.description = trip.getDescription();
		this.startDate = trip.getStartDate();
		this.endDate = trip.getEndDate();
		this.shareYn = trip.getShareYn();
	}

}
