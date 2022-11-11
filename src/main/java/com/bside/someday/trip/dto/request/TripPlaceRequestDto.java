package com.bside.someday.trip.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.trip.entity.TripEntry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripPlaceRequestDto extends PlaceRequestDto {

	private Long placeId;
	private Integer placeSn;
	private String visitDate;

	public TripEntry toTripEntity(Place place) {
		return TripEntry.builder()
			.place(place)
			.placeSn(placeSn)
			.visitDate(LocalDate.parse(visitDate, DateTimeFormatter.ISO_DATE)) // yyyy-MM-dd
			.build();
	}
}
