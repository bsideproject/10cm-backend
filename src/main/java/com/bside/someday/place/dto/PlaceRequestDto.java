package com.bside.someday.place.dto;

import com.bside.someday.place.entity.Place;
import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
@Getter
public class PlaceRequestDto {
    @NotNull(message = "name 필드는 필수입니다.")
    private String name;
    private String address;
    private String addressDetail;
    private String phone;
    private String[] tag;
    private String description;
    @NotNull(message = "longitude 필드는 필수입니다.")
    private String longitude;
    @NotNull(message = "latitude 필드는 필수입니다.")
    private String latitude;

    private Long imageId;

    private User user;///////////>?


    public Place toEntity() {
        return Place.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone(phone)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .imageId(imageId)
                .user(user)
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
                .imageId(imageId)
                .user(user)
                .build();
    }

//    @Builder
//    public PlaceRequestDto(String name, String address, String addressDetail,
//                           String phone, String[] tag, String description,
//                           String latitude, String longitude) {
//        this.name = name;
//        this.address = address;
//        this.addressDetail = addressDetail;
//        this.phone = phone;
//        this.tag = tag;
//        this.description = description;
//        this.latitude = latitude;
//        this.longitude = longitude;
//    }

    public void addUser(User user) {
        this.user = user;
    }

}
