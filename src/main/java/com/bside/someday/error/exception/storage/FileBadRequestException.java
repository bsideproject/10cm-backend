package com.bside.someday.error.exception.storage;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class FileBadRequestException extends BusinessException {

	private static final long serialVersionUID = 500565520596223664L;

	public FileBadRequestException() {
		super(FILE_BAD_REQUEST);
	}

	public FileBadRequestException(String message) {
		super(message, FILE_BAD_REQUEST);
	}
}
