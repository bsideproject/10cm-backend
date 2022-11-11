package com.bside.someday.trip.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.bside.someday.common.entity.BaseEntity;
import com.bside.someday.place.entity.Place;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TripEntry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tripEntryId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@Column
	private int placeSn;

	private LocalDate visitDate;


	@OneToOne
	@JoinColumn(name = "place_id")
	private Place place;

	public TripEntry setTrip(Trip trip) {
		this.trip = trip;

		// 무한루프 방지
		if (!trip.getTripEntryList().contains(this)) {
			trip.getTripEntryList().add(this);
		}
		return this;
	}

	@Builder
	public TripEntry(Long tripEntryId, int placeSn, LocalDate visitDate, Place place) {
		this.tripEntryId = tripEntryId;
		this.placeSn = placeSn;
		this.visitDate = visitDate;
		this.place = place;
	}
}
