package com.bside.someday.trip.dto.request;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class TripRequestDto extends PageRequest {

	private static final long serialVersionUID = -6214974221063831396L;
	public static final int DEFAULT_PAGE_SIZE = 6;
	public static final String DEFAULT_SORT_PROPERTY = "createdDate";
	public static final Sort DEFAULT_SORT = Sort.by(DESC, DEFAULT_SORT_PROPERTY);

	/**
	 * Creates a new {@link PageRequest} with sort parameters applied.
	 *
	 * @param page zero-based page index, must not be negative.
	 * @param size the size of the page to be returned, must be greater than 0.
	 * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
	 */
	protected TripRequestDto(int page, int size, Sort sort) {
		super(page == 0 ? 0 : page - 1, size, sort);
	}

	public static TripRequestDto of(int page) {
		return new TripRequestDto(page, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
	}

	public static TripRequestDto of(int page, int size) {
		return new TripRequestDto(page, size, DEFAULT_SORT);
	}

	public static TripRequestDto of(int page, int size, Sort sort) {
		return new TripRequestDto(page, size, sort);
	}
}
