package com.bside.someday.common.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
			page.getNumber() + 1,
			page.getSize(),
			page.getTotalPages(),
			page.isFirst(),
			page.isLast()
		);

	}

	public static <T> ResponseEntity<PageDto<T>> ok(final Page<T> page) {
		return ResponseEntity.ok(new PageDto<>(page));
	}
}
