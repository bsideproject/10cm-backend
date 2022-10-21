package com.bside.someday.storage.exception;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadFailException  extends BusinessException {

	private static final long serialVersionUID = -7218942564126088809L;

	public FileUploadFailException() {
		super(FILE_UPLOAD_FAIL);
	}

}
