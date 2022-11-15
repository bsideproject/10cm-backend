package com.bside.someday.trip.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

	@NotBlank(message = "여행 제목이 입력되지 않았습니다.")
	@Size(max = 20, message = "여행 제목은 20자 이내로 입력해주세요.")
	private String tripName;

	private String description;

	@NotBlank(message = "시작일이 입력되지 않았습니다.")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "여행 시작일 타입 오류입니다. (yyyy-MM-dd)")
	private String startDate;

	@NotBlank(message = "종료일이 입력되지 않았습니다.")
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "여행 종료일 타입 오류입니다. (yyyy-MM-dd)")
	private String endDate;

	@Pattern(regexp = "Y|N", message = "여행 공유 여부 타입 오류입니다. (Y or N)")
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
