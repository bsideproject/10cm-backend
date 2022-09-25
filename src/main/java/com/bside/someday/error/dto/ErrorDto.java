package com.bside.someday.error.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorDto {

	private final String message;
	private final String code;
	private final int status;

	public ErrorDto(String message, String code, HttpStatus status) {
		this.message = message;
		this.code = code;
		this.status = status.value();
	}

	public ErrorDto(ErrorType type) {
		this.message = type.getMessage();
		this.code = type.getCode();
		this.status = type.getStatus().value();
	}
}
