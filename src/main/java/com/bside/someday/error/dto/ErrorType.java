package com.bside.someday.error.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "허용되지 않은 요청입니다.", "ATH001"),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.", "ATH002"),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", "ATH003"),
	NOT_ALLOW_ACCESS(HttpStatus.UNAUTHORIZED, "요청 권한이 없습니다.", "ATH004"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원정보가 존재하지 않습니다.", "USR001"),
	UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생하였습니다.", "ERO001"),

	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다.", "FIL001"),
	FILE_UPLOAD_FAIL(HttpStatus.BAD_GATEWAY, "파일 업로드 중 오류가 발생하였습니다.", "FIL002"),
	;


	private final HttpStatus status;
	private final String message;
	private final String code;
}
