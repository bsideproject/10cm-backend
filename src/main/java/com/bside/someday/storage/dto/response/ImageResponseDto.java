package com.bside.someday.storage.dto.response;

import com.bside.someday.storage.entity.ImageData;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ImageResponseDto {

	private final Long imageId;

	private final String storedName;

	private final String originalName;
	private final String url;

	public ImageResponseDto(ImageData imageData) {
		this.imageId = imageData.getId();
		this.url = imageData.getUrl();
		this.storedName = imageData.getName();
		this.originalName = imageData.getOriginalName();
	}
}
