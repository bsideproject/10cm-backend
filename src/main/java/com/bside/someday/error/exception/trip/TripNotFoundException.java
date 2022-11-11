package com.bside.someday.error.exception.trip;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class TripNotFoundException extends BusinessException {
	public TripNotFoundException() {
		super(TRIP_NOT_FOUND);
	}

	public TripNotFoundException(String message) {
		super(message, TRIP_NOT_FOUND);
	}
}
