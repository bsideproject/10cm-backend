package com.bside.someday.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@Getter
public class PlaceIdResponseDto {
    private Long id;

    @Builder
    public PlaceIdResponseDto(Long id) {
        this.id = id;
    }
}
