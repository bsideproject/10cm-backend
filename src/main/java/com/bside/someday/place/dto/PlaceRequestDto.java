package com.bside.someday.place.dto;

import com.bside.someday.place.entity.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class PlaceRequestDto {
    private String name;
    private String address;
    private String addressDetail;
    private String image;
    private String phone;
    private String[] tag;
    private String memo;
    private String longitude;
    private String latitude;

    public Place toEntity() {
        return Place.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .image(image)
                .phone(phone)
                .memo(memo)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    public Place toEntity(Long id) {
        return Place.builder()
                .id(id)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .image(image)
                .phone(phone)
                .memo(memo)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

}
