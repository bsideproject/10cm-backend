package com.bside.someday.error.exception.storage;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class FileNotFoundException extends BusinessException {

	private static final long serialVersionUID = -5292594989203026858L;

	public FileNotFoundException() {
		super(FILE_NOT_FOUND);
	}
}