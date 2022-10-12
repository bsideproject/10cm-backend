package com.bside.someday.place.dto;

import com.bside.someday.error.dto.ErrorType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDto {

	private final String message;
	private final String code;
	private final int status;

	public ResponseDto() {
		this.message = "success";
		this.code = "SUC01";
		this.status = 200;
	}

}
