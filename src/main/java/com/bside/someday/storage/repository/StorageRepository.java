package com.bside.someday.storage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bside.someday.storage.entity.ImageData;

@Repository
public interface StorageRepository extends JpaRepository<ImageData, Long> {

	Optional<ImageData> findByName(String name);

}
