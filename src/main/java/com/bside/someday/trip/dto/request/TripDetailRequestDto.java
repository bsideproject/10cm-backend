package com.bside.someday.trip.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bside.someday.trip.entity.Trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripDetailRequestDto {

	private Long tripId;

	private String tripName;

	private String description;

	private String startDate;

	private String endDate;

	private String shareYn = "N";

	private List<TripPlaceRequestDto> placeList;


	public Trip toEntity() {
		return Trip.builder()
			.tripId(tripId)
			.tripName(tripName)
			.description(description)
			.startDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE))
			.endDate(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE))
			.shareYn(shareYn)
			.build();
	}

}
