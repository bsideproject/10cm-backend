package com.bside.someday.trip.entity;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.bside.someday.common.entity.BaseEntity;
import com.bside.someday.error.exception.trip.TripInvalidParameterException;
import com.bside.someday.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tripId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(length = 300)
	private String tripName;

	@Column(length = 1000)
	private String description;

	private LocalDate startDate;

	private LocalDate endDate;

	@Column(columnDefinition = "char(1) default 'N'")
	private String shareYn;

	@OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<TripEntry> tripEntryList = new ArrayList<>();

	@Transient
	private int tripPeriod = 0;

	public static Trip createTrip(Trip trip, User user, List<TripEntry> tripEntries) {

		if (trip == null) {
			throw new TripInvalidParameterException("잘못된 요청입니다.(여행정보 없음)");
		}

		if (trip.getTripPeriod() <= 0) {
			throw new TripInvalidParameterException("시작일이 종료일보다 클 수 없습니다.");
		}

		trip.user = user;

		if (trip.getTripPeriod() != tripEntries.size()) {
			log.info("trip.getTripPeriod() : {} <> tripEntries.size(): {}", trip.getTripPeriod(), tripEntries.size());
			throw new TripInvalidParameterException("잘못된 요청입니다.(기간 불일치)");
		}


		tripEntries.forEach(trip::addTripDetail);

		return trip;
	}

	public static Trip updateTrip(Long tripId, Trip trip, User user, List<TripEntry> tripEntries) {

		if (trip == null) {
			throw new TripInvalidParameterException("잘못된 요청입니다.(여행정보 없음)");
		}

		trip.tripId = tripId;

		return createTrip(trip, user, tripEntries);
	}

	public void addTripDetail(TripEntry tripEntry) {
		this.tripEntryList.add(tripEntry);

		// 무한루프 방지
		if (tripEntry.getTrip() != this) {
			tripEntry.setTrip(this);
		}
	}

	@Builder
	public Trip(Long tripId, String tripName, String description, LocalDate startDate, LocalDate endDate,
		String shareYn) {
		this.tripId = tripId;
		this.tripName = tripName;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.shareYn = shareYn;
	}

	public int getTripPeriod() {
		if (tripPeriod == 0) {
			tripPeriod = Period.between(this.startDate, this.endDate).getDays() + 1;
		}
		return tripPeriod;
	}
}

