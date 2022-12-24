package com.bside.someday.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class PlaceListResponseDto {
    private long count;
    private List<PlaceResponseDto> placeList;

    @Builder
    public PlaceListResponseDto(List<PlaceResponseDto> placeList, long count) {
        this.count = count;
        this.placeList = placeList;
    }
}
