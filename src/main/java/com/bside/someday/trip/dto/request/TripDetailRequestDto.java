package com.bside.someday.trip.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.bside.someday.trip.entity.Trip;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(description = "여행 정보를 등록하거나 수정할 때 사용")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TripDetailRequestDto {

	@NotBlank(message = "여행 제목이 입력되지 않았습니다.")
	@Size(max = 30, message = "여행 제목은 30자 이내로 입력해주세요.")
	private String name;

	@Size(max = 1000, message = "여행 메모는 1000자 이내로 입력해주세요.")
	private String description;

	@NotBlank(message = "시작일이 입력되지 않았습니다.")
	@Pattern(regexp = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])", message = "여행 시작일 타입 오류입니다. (yyyy-MM-dd)")
	private String startDate;

	@NotBlank(message = "종료일이 입력되지 않았습니다.")
	@Pattern(regexp = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])", message = "여행 종료일 타입 오류입니다. (yyyy-MM-dd)")
	private String endDate;

	@Pattern(regexp = "[YN]", message = "여행 공유 여부 타입 오류입니다. (Y or N)")
	private String shareYn = "N";

	@Size(max = 1000, message = "잘못된 이미지 URL입니다.")
	private String tripImageUrl;

	@Size(max = 500, message = "잘못된 이미지 파일명입니다.")
	private String tripImageName;

	@NotNull
	private List<List<@Valid TripDetails>> tripDetails = new ArrayList<>();

	public Trip toEntity() {
		return Trip.builder()
			.tripName(name)
			.description(description)
			.tripImageUrl(tripImageUrl)
			.tripImageName(tripImageName)
			.startDate(LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE))
			.endDate(LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE))
			.shareYn(shareYn)
			.build();
	}

}
