package com.bside.someday.trip.dto.response;

import com.bside.someday.trip.entity.TripPlace;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TripPlaceResponseDto {

	private final String id;

	private final String name;

	private final String description;

	private final String phone;

	private final String address;

	private final String addressDetail;

	private final String longitude;

	private final String latitude;

	public TripPlaceResponseDto(TripPlace tripPlace) {
		this.id = tripPlace.getPlaceUid();
		this.name = tripPlace.getName();
		this.description = tripPlace.getDescription();
		this.address = tripPlace.getAddress();
		this.addressDetail = tripPlace.getAddressDetail();
		this.phone = tripPlace.getPhone();
		this.longitude = tripPlace.getLongitude();
		this.latitude = tripPlace.getLatitude();
	}
}
