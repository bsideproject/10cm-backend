package com.bside.someday.trip.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class TripRequestDto extends PageRequest {

	private static final long serialVersionUID = -6214974221063831396L;

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 *
	 * @param page zero-based page index, must not be negative.
	 * @param size the size of the page to be returned, must be greater than 0.
	 * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
	 */
	protected TripRequestDto(int page, int size, Sort sort) {
		super(page, size, sort);
	}
}
