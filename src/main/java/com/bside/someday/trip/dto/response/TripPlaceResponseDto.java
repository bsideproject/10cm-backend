package com.bside.someday.trip.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.trip.entity.TripEntry;

import lombok.Getter;

@Getter
public class TripPlaceResponseDto extends PlaceResponseDto {

	private final int placeSn;

	private final LocalDate visitDate;

	public TripPlaceResponseDto(TripEntry tripEntry, Place place) {
		super(place, new String[] {});
		this.placeSn = tripEntry.getPlaceSn();
		this.visitDate = tripEntry.getVisitDate();
	}
}
