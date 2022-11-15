package com.bside.someday.trip.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bside.someday.trip.entity.Trip;
import com.bside.someday.trip.entity.TripEntry;

@Repository
public interface TripDetailRepository extends JpaRepository<TripEntry, Long> {

	List<TripEntry> findAllByTrip(Trip trip);
}
