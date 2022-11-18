package com.bside.someday.common.dto;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseDto<T> implements Serializable {

	private static final long serialVersionUID = -5287343343818460795L;

	public static <T> ResponseEntity<T> ok(T data) {
		return ResponseEntity.ok(data);
	}

	public static <T> ResponseEntity<T> created(T data) {
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(data);
	}

	public static ResponseEntity<Void> noContent() {
		return ResponseEntity
			.status(HttpStatus.NO_CONTENT)
			.build();
	}

}
