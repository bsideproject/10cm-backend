package com.bside.someday.user.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserNicknameResponseDto {

	@Schema(description = "사용자 닉네임")
	private String nickname;

	@Schema(description = "중복 여부")
	private boolean isDuplicated;

}
