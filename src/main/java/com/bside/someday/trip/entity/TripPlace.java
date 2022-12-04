package com.bside.someday.trip.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.bside.someday.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripPlace extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tripPlaceId;

	// 장소 고유 아이디
	@Column(length = 20)
	private String placeUid;

	@Column(length = 300)
	private String name;

	@Column(length = 500)
	private String address;

	@Column(length = 500)
	private String addressDetail;

	@Column(length = 20)
	private String phone;

	@Column(length = 1000)
	private String description;

	private String longitude;

	private String latitude;

	private int placeSn = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_entry_id")
	private TripEntry tripEntry;

	public void setTripEntry(TripEntry tripEntry) {
		this.tripEntry = tripEntry;
	}

	@Builder
	public TripPlace(Long tripPlaceId, String placeUid, String name, String address, String addressDetail, String phone,
		String description, String longitude, String latitude, int placeSn) {
		this.tripPlaceId = tripPlaceId;
		this.placeUid = placeUid;
		this.name = name;
		this.address = address;
		this.addressDetail = addressDetail;
		this.phone = phone;
		this.description = description;
		this.longitude = longitude;
		this.latitude = latitude;
		this.placeSn = placeSn;
	}

}

