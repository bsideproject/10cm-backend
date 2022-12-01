package com.bside.someday.trip.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.bside.someday.trip.entity.Trip;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TripResponseDto {

	private final Long tripId;

	private final String name;

	private final String description;

	private final String startDate;

	private final String endDate;

	private final String tripImageUrl;

	private final String shareYn;

	private final LocalDateTime createdDate;

	private final LocalDateTime modifiedDate;

	public TripResponseDto(Trip trip) {
		this.tripId = trip.getTripId();
		this.name = trip.getTripName();
		this.description = trip.getDescription();
		this.startDate = trip.getStartDate().format(DateTimeFormatter.ISO_DATE);
		this.endDate = trip.getEndDate().format(DateTimeFormatter.ISO_DATE);
		this.tripImageUrl = trip.getTripImageUrl();
		this.shareYn = trip.getShareYn();
		this.createdDate = trip.getCreatedDate();
		this.modifiedDate = trip.getModifiedDate();
	}
}
