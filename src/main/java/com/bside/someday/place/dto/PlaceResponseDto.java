package com.bside.someday.place.dto;

import com.bside.someday.place.entity.Place;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@Getter
public class PlaceResponseDto {
    private String name;
    private String address;
    private String addressDetail;
    private String phone;
    private String[] tag;
    private String description;
    private String longitude;
    private String latitude;
    private Long imageId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public PlaceResponseDto(Place place, String[] tag) {
        this.name = place.getName();
        this.address = place.getAddress();
        this.addressDetail = place.getAddressDetail();
        this.phone = place.getPhone();
        this.tag = tag;
        this.description = place.getDescription();
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.imageId = place.getImageId();
        this.createdDate = place.getCreatedDate();
        this.modifiedDate = place.getModifiedDate();
    }
//    @Builder
//    public PlaceResponseDto(String name, String address, String addressDetail, String phone,
//                            String[] tag, String description, String longitude, String latitude,
//                            Long imageId, LocalDateTime createdDate, LocalDateTime modifiedDate) {
//        this.name = name;
//        this.address = address;
//        this.addressDetail = addressDetail;
//        this.phone = phone;
//        this.tag = tag;
//        this.description = description;
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.imageId = imageId;
//        this.createdDate = createdDate;
//        this.modifiedDate = modifiedDate;
//    }
}
