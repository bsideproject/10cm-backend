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
    private String phone;
    private String[] tag;
    private String description;
    private String longitude;
    private String latitude;


    public Place toEntity() {
        return Place.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone(phone)
                .description(description)
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
                .phone(phone)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    @Builder
    public PlaceRequestDto(String name, String address, String addressDetail,
                           String phone, String[] tag, String description,
                           String latitude, String longitude, int page, int size) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phone = phone;
        this.tag = tag;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
