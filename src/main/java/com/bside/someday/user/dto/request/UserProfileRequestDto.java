package com.bside.someday.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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

	@NotBlank(message = "닉네임이 입력되지 않았습니다.")
	@Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이내로 입력해주세요.")
	@Pattern(regexp = "^[가-힣ㄱ-ㅎa-zA-Z0-9_-]*$", message = "닉네임에 올바르지 않은 형식의 문자열이 입력되었습니다.")
	@ApiModelProperty(value = "사용자 닉네임", required = true)
	private String nickname;

	@ApiModelProperty(value = "사용자 프로필 이미지 URL")
	@Size(max = 500, message = "URL을 다시 한번 확인해주세요")
	private String profileImageUrl;
}

