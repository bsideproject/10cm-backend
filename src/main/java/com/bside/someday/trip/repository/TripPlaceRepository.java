package com.bside.someday.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bside.someday.trip.entity.TripPlace;

@Repository
public interface TripPlaceRepository extends JpaRepository<TripPlace, Long> {

}
