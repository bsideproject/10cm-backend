package com.bside.someday.trip.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TripDetailResponseDto {

	private final long tripId;

	private final String name;

	private final String description;

	private final LocalDate startDate;

	private final LocalDate endDate;

	private final String shareYn;

	private final String tripImageUrl;

	private final List<List<TripPlaceResponseDto>> tripDetails = new ArrayList<>();

	private final LocalDateTime createdDate;

	private final LocalDateTime modifiedDate;

	public TripDetailResponseDto(Trip trip, List<TripEntry> tripEntryList) {

		this(trip);

		for (TripEntry tripEntry : tripEntryList) {
			tripDetails.add(tripEntry.getTripPlaceList()
				.stream()
				.map(TripPlaceResponseDto::new)
				.collect(Collectors.toList()));
		}
	}

	public TripDetailResponseDto(Trip trip) {
		this.tripId = trip.getTripId();
		this.name = trip.getTripName();
		this.description = trip.getDescription();
		this.startDate = trip.getStartDate();
		this.endDate = trip.getEndDate();
		this.shareYn = trip.getShareYn();
		this.tripImageUrl = trip.getTripImageUrl();
		this.createdDate = trip.getCreatedDate();
		this.modifiedDate = trip.getModifiedDate();
	}


}
