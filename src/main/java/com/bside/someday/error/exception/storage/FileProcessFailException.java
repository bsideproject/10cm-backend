package com.bside.someday.error.exception.storage;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileProcessFailException extends BusinessException {

	private static final long serialVersionUID = 5114121425359187337L;

	public FileProcessFailException() {
		super(FILE_PROCESS_FAIL);
	}

	public FileProcessFailException(String message) {
		super(message, FILE_PROCESS_FAIL);
	}
}
