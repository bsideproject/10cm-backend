package com.bside.someday.trip.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;

import lombok.Getter;

@Getter
public class TripDetailResponseDto {

	private final long tripId;

	private final String tripName;

	private final String description;

	private final LocalDate startDate;

	private final LocalDate endDate;

	private final String shareYn;
	private List<TripPlaceResponseDto> placeList;

	public TripDetailResponseDto(Trip trip, List<TripEntry> tripEntryList) {

		this(trip);

		this.placeList = tripEntryList.stream().map(
			(TripEntry tripEntry) -> new TripPlaceResponseDto(tripEntry, tripEntry.getPlace())).collect(
			Collectors.toList());
	}
	public TripDetailResponseDto(Trip trip) {
		this.tripId = trip.getTripId();
		this.tripName = trip.getTripName();
		this.description = trip.getDescription();
		this.startDate = trip.getStartDate();
		this.endDate = trip.getEndDate();
		this.shareYn = trip.getShareYn();
	}

}
