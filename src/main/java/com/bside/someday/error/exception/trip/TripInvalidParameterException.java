package com.bside.someday.error.exception.trip;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class TripInvalidParameterException extends BusinessException {

	private static final long serialVersionUID = 6902920209070856932L;

	public TripInvalidParameterException() {
		super(TRIP_INVALID_PARAMETER);
	}

	public TripInvalidParameterException(String message) {
		super(message, TRIP_INVALID_PARAMETER);
	}
}
