package com.bside.someday.place.repository;

import com.bside.someday.place.entity.PlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceTagRepository extends JpaRepository<PlaceTag, Long> {
    List<PlaceTag> findByPlaceId(Long placeId);

    void deleteByPlaceId(Long placeId);
}
