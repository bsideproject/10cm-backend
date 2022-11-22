package com.bside.someday.place.repository;

import com.bside.someday.place.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdAndUser_UserId(Long id, Long userId);

    Page<Place> findAllByUser_UserId(Pageable pageable, Long userId);

    int deleteByIdAndUser_UserId(Long id, Long userId);

    int countByUser_UserId(Long userId);
}
