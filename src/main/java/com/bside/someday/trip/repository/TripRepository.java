package com.bside.someday.trip.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bside.someday.trip.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

	@Query(value = "select t from Trip t join fetch t.tripEntryList e where t.tripId =:tripId")
	Trip findByIdUsingFetchJoin(@Param("tripId") Long tripId);

	@Query("select t from Trip t where t.user.userId =:userId")
	Page<Trip> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

}
