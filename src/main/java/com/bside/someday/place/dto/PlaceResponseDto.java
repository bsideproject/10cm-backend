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
    private String image;
    private String phone;
    private String[] tag;
    private String memo;
    private String longitude;
    private String latitude;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public PlaceResponseDto(Place place, String[] tag) {
        this.name = place.getName();
        this.address = place.getAddress();
        this.addressDetail = place.getAddressDetail();
        this.image = place.getImage();
        this.phone = place.getPhone();
        this.tag = tag;
        this.memo = place.getMemo();
        this.longitude = place.getLongitude();
        this.latitude = place.getLatitude();
        this.createdDate = place.getCreatedDate();
        this.modifiedDate = place.getModifiedDate();
    }
//    @Builder
//    public PlaceResponseDto(String name, String address, String addressDetail, String image, String phone,
//                            String[] tag, String memo, String longitude, String latitude,
//                            LocalDateTime createdDate, LocalDateTime modifiedDate) {
//        this.name = name;
//        this.address = address;
//        this.addressDetail = addressDetail;
//        this.image = image;
//        this.phone = phone;
//        this.tag = tag;
//        this.memo = memo;
//        this.longitude = longitude;
//        this.latitude = latitude;
//        this.createdDate = createdDate;
//        this.modifiedDate = modifiedDate;
//    }
}
