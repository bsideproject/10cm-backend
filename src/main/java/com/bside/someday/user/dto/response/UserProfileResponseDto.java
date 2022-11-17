package com.bside.someday.user.dto.response;

import com.bside.someday.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResponseDto {

	@Schema(description = "사용자 아이디")
	private Long userId;

	@Schema(description = "사용자 이름")
	private String name;

	@Schema(description = "사용자 닉네임")
	private String nickname;

	@Schema(description = "사용자 이메일")
	private String email;

	@Schema(description = "프로필 사진 URL")
	private String profileImageUrl;

	public UserProfileResponseDto(User user) {
		this.userId = user.getUserId();
		this.name = user.getName();
		this.nickname = user.getNickname();
		this.email = user.getEmail();
		this.profileImageUrl = user.getProfileImage();
	}
}
