package com.bside.someday.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bside.someday.place.entity.BaseTimeEntity;
import com.bside.someday.user.dto.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@EqualsAndHashCode
@ToString
@Table(name = "`user`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

	@JsonIgnore
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "email", length = 500)
	private String email;

	@Column(name = "nickname", length = 200)
	private String nickname;

	@Column(name = "profile_image", length = 500)
	private String profileImage;

	@Column(name = "social_id", length = 200)
	private String socialId;

	@Column(name = "social_type", length = 50)
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	@Column(name = "profile_upload_yn", columnDefinition = "char(1) default 'N'")
	private String profileUploadYn = "N";

	@Builder
	public User(Long userId, String name, String email, String nickname, String profileImage, String socialId,
		SocialType socialType) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.socialId = socialId;
		this.socialType = socialType;
	}

	public User update(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
		return this;
	}

	public User updateNickName(String nickname) {
		this.nickname = nickname;
		return this;
	}
	public User updateProfileImage(String profileImage) {
		this.profileImage = profileImage;
		this.profileUploadYn = "Y";
		return this;
	}

	public User delete() {
		this.name = null;
		this.email = null;
		this.nickname = null;
		this.profileImage = null;
		this.socialId = null;
		this.socialType = null;
		return this;
	}

}

