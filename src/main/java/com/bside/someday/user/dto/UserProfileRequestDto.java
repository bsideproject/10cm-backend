package com.bside.someday.user.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDto {

	@NotNull
	@ApiModelProperty(value = "사용자 아이디", required = true)
	private Long userId;

	@NotNull
	@ApiModelProperty(value = "사용자 닉네임", required = true)
	private String nickname;

	@ApiModelProperty(value = "사용자 프로필 이미지 URL", required = true)
	private String profileImageUrl;

}

