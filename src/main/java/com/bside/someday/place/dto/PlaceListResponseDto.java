package com.bside.someday.place.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
public class PlaceListResponseDto {
    private int count;
    private List<PlaceResponseDto> placeList;

    @Builder
    public PlaceListResponseDto(List<PlaceResponseDto> placeList) {
        this.count = placeList.size();
        this.placeList = placeList;
    }
}
