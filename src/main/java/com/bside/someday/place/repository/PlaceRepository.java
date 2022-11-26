package com.bside.someday.place.repository;

import com.bside.someday.place.entity.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByIdAndUser_UserId(Long id, Long userId);

    Page<Place> findAllByUser_UserId(Pageable pageable, Long userId);

    @Query(value = "SELECT p FROM Place p, PlaceTag pt, Tag t " +
            "LEFT OUTER JOIN User u ON p.user.userId=u.userId  " +
            "WHERE u.userId = :userId " +
            "AND p.id = pt.placeId " +
            "AND t.id = pt.tagId " +
            "AND t.name LIKE CONCAT('%',:tag,'%')")
    Page<Place> findAllByUser_UserIdTag(Pageable pageable, @Param(value="userId") Long userId, @Param(value="tag") String tag);

    int deleteByIdAndUser_UserId(Long id, Long userId);

    int countByUser_UserId(Long userId);

    @Query(value = "SELECT count(p) FROM Place p, PlaceTag pt, Tag t " +
            "LEFT OUTER JOIN User u ON p.user.userId=u.userId  " +
            "WHERE u.userId = :userId " +
            "AND p.id = pt.placeId " +
            "AND t.id = pt.tagId " +
            "AND t.name LIKE CONCAT('%',:tag,'%')")
    int countByUser_UserIdTag(@Param(value="userId") Long userId, @Param(value="tag") String tag);
}
