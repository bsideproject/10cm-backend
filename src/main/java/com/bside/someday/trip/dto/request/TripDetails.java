package com.bside.someday.trip.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.bside.someday.trip.entity.TripPlace;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@ApiModel(description = "여행 등록 또는 수정시 요청하는 장소 리스트")
public class TripDetails {

	@NotBlank(message = "장소명이 입력되지 않았습니다.")
	@Size(max = 30, message = "장소명은 30자 이내로 입력해주세요.")
	private String name;

	@Size(max = 1000, message = "장소 설명은 1000자 이내로 입력해주세요.")
	private String description;

	@Size(max = 1000, message = "주소는 1000자 이내로 입력해주세요.")
	private String address;

	@Size(max = 1000, message = "주소 상세는 1000자 이내로 입력해주세요.")
	private String addressDetail;

	@NotBlank
	@Pattern(regexp = "^[0-9]*[.][0-9]*$", message = "올바르지 않은 형식의 좌표 값이 입력되었습니다.")
	private String longitude;

	@NotBlank
	@Pattern(regexp = "^[0-9]*[.][0-9]*$", message = "올바르지 않은 형식의 좌표 값이 입력되었습니다.")
	private String latitude;

	public TripPlace toEntity(int placeSn) {

		return TripPlace.builder()
			.placeSn(placeSn)
			.name(this.name)
			.description(this.description)
			.longitude(this.longitude)
			.latitude(this.latitude)
			.address(this.address)
			.addressDetail(this.addressDetail)
			.build();
	}

}