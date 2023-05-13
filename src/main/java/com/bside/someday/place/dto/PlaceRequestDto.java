package com.bside.someday.place.dto;

import com.bside.someday.place.entity.Place;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Getter
public class PlaceRequestDto {
    @NotNull(message = "name 필드는 필수입니다.")
    private String name;
    private String address;
    private String addressDetail;
    private String roadAddress;
    @Pattern(regexp = "^([0-9]{2,3}[-][0-9]{3,4}[-][0-9]{4}|)|([0-9]{4}[-][0-9]{4})|([0-9]{8,12})$", message = "올바르지 않은 형식의 전화번호 값이 입력되었습니다.")
    private String phone;
    private String[] tag;
    private String description;
    @NotNull(message = "longitude 필드는 필수입니다.")
    private String longitude;
    @NotNull(message = "latitude 필드는 필수입니다.")
    private String latitude;

    private String image;

    public Place toEntity() {
        return Place.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .roadAddress(roadAddress)
                .phone(phone)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .image(image)
                .build();
    }

    public Place toEntity(Long id) {
        return Place.builder()
                .id(id)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .roadAddress(roadAddress)
                .phone(phone)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .image(image)
                .build();
    }

    @Builder
    public PlaceRequestDto(String name, String address, String addressDetail, String roadAddress,
                           String phone, String[] tag, String description,
                           String image, String latitude, String longitude) {
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.roadAddress = roadAddress;
        this.phone = phone;
        this.tag = tag;
        this.description = description;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
