package com.bside.someday.trip.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
public class TripPlaceRequestDto {

	@NotNull(message = "장소가 선택되지 않았습니다.")
	private Long placeId;

	@NotNull(message = "순번이 지정되지 않았습니다.")
	@Min(value = 1, message = "순번은 1 보다 작은 값이 올 수 없습니다.")
	@Max(value = 1000, message = "순번은 1,000 보다 큰 값이 올 수 없습니다.")
	private Integer placeSn;

	@NotBlank(message = "방문일자가 선택되지 않았습니다.")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "방문일 타입 오류입니다. (yyyy-MM-dd)")
	private String visitDate;

	public TripEntry toTripEntity(Place place) {
		return TripEntry.builder()
			.place(place)
			.placeSn(placeSn)
			.visitDate(LocalDate.parse(visitDate, DateTimeFormatter.ISO_DATE)) // yyyy-MM-dd
			.build();
	}
}
