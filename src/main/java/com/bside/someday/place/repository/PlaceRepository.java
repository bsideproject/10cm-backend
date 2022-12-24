package com.bside.someday.place.repository;

import com.bside.someday.place.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdAndUser_UserId(Long id, Long userId);

    Page<Place> findAllByUser_UserId(Pageable pageable, Long userId);

    int deleteByIdAndUser_UserId(Long id, Long userId);

    int countByUser_UserId(Long userId);

    @Query(value = "select pt.placeId from Place p, Tag t, PlaceTag pt " +
            "where t.id = pt.tagId " +
            "and p.id = pt.placeId " +
            "and t.name LIKE CONCAT('%',:tag,'%') " +
            "and p.user.userId = :userId " +
            "group by pt.placeId")
    List<Long> findByTagContains(@Param(value="userId") Long userId, @Param(value="tag") String tag);

    Page<Place> findByIdIn(Pageable pageable, List<Long> placeIdList);
}
