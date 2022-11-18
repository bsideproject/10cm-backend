package com.bside.someday.trip.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.bside.someday.common.entity.BaseEntity;
import com.bside.someday.error.exception.trip.TripInvalidParameterException;

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

	@OneToMany(mappedBy = "tripEntry", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<TripPlace> tripPlaceList = new ArrayList<>();

	public static TripEntry createTripEntry(TripEntry tripEntry, List<TripPlace> tripPlaceList) {

		if (tripEntry == null) {
			throw new TripInvalidParameterException("잘못된 요청입니다.(여행 엔트리 없음)");
		}

		tripPlaceList.forEach(tripEntry::addTripPlace);

		return tripEntry;
	}

	public void addTripPlace(TripPlace tripPlace) {
		this.tripPlaceList.add(tripPlace);

		// 무한루프 방지
		if (tripPlace.getTripEntry() != this) {
			tripPlace.setTripEntry(this);
		}
	}
	private int entrySn = 1;

	@Builder
	public TripEntry(Long tripEntryId, int entrySn) {
		this.tripEntryId = tripEntryId;
		this.entrySn = entrySn;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}
}
