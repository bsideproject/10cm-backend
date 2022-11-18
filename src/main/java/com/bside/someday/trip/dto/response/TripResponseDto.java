package com.bside.someday.trip.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.bside.someday.trip.entity.Trip;

import lombok.Getter;

@Getter
public class TripResponseDto {

	private Long tripId;

	private String name;

	private String description;

	private String startDate;

	private String endDate;

	private String shareYn;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;

	public TripResponseDto(Trip trip) {
		this.tripId = trip.getTripId();
		this.name = trip.getTripName();
		this.description = trip.getDescription();
		this.startDate = trip.getStartDate().format(DateTimeFormatter.ISO_DATE);
		this.endDate = trip.getEndDate().format(DateTimeFormatter.ISO_DATE);
		this.shareYn = trip.getShareYn();
		this.createdDate = trip.getCreatedDate();
		this.modifiedDate = trip.getModifiedDate();
	}
}
