package com.bside.someday.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Optional<User> findUserBySocialIdAndSocialType(String socialId, SocialType socialType);
}
