package com.bside.someday.trip.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.bside.someday.common.entity.BaseEntity;
import com.bside.someday.error.exception.trip.TripInvalidParameterException;
import com.bside.someday.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private List<TripEntry> tripEntryList = new ArrayList<>();

	// 무한루프 방지
	public void addTripDetail(TripEntry tripEntry) {
		this.tripEntryList.add(tripEntry);
		if (tripEntry.getTrip() != this) {
			tripEntry.setTrip(this);
		}
	}

	public static Trip createTrip(Trip trip, User user, TripEntry... tripEntries) {

		if (trip == null) {
			trip = new Trip();
		}
		trip.user = user;
		Arrays.stream(tripEntries).forEach(trip::addTripDetail);

		return trip;
	}

	public static Trip createTrip(Trip trip, User user, List<TripEntry> tripEntries) {

		if (trip == null) {
			trip = new Trip();
		}
		trip.user = user;
		tripEntries.stream().forEach(trip::addTripDetail);

		return trip;
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

		if (startDate.isAfter(endDate)) {
			throw new TripInvalidParameterException("시작일이 종료일보다 클 수 없습니다.");
		}
	}
}

