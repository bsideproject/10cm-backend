package com.bside.someday.place.dto;

import com.bside.someday.place.entity.Place;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@Getter
public class PlaceResponseDto {
    private Long id;
    private String name;
    private String address;
    private String addressDetail;
    private String roadAddress;
    private String phone;
    private String[] tag;
    private String description;
    private String longitude;
    private String latitude;
    private String image;
    private String homepage;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public PlaceResponseDto(Place place, String[] tag) {
        this.id = place.getId();
        this.name = place.getName();
        this.address = place.getAddress();
        this.addressDetail = place.getAddressDetail();
        this.roadAddress = place.getRoadAddress();
        this.phone = place.getPhone();
        this.tag = tag;
        this.description = place.getDescription();
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.image = place.getImage();
        this.homepage = place.getHomepage();
        this.createdDate = place.getCreatedDate();
        this.modifiedDate = place.getModifiedDate();
    }
}
