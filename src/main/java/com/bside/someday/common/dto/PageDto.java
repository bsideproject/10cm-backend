package com.bside.someday.common.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class PageDto<T> implements Serializable {

	private static final long serialVersionUID = -8752244352831410009L;

	private List<T> data;

	private int page;

	private int size;

	private int totalPages;

	private boolean first;

	private boolean last;

	private PageDto(final Page<T> page) {
		this(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalPages(),
			page.isFirst(),
			page.isLast()
		);
	}

	PageDto(List<T> content, int number, int size, int totalPages, boolean first, boolean last) {
		this.data = content;
		this.page = number;
		this.size = size;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	public static <T> ResponseEntity<PageDto<T>> ok(final Page<T> page) {
		return ResponseEntity.ok(new PageDto<>(page));
	}
}
