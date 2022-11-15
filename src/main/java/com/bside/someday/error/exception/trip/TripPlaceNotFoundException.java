package com.bside.someday.error.exception.trip;

import static com.bside.someday.error.dto.ErrorType.*;

import com.bside.someday.error.exception.BusinessException;

public class TripPlaceNotFoundException extends BusinessException {
	public TripPlaceNotFoundException() {
		super(TRIP_PLACE_NOT_FOUND);
	}

	public TripPlaceNotFoundException(String message) {
		super(message, TRIP_PLACE_NOT_FOUND);
	}
}
