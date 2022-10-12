package com.bside.someday.place.entity;

import com.bside.someday.place.dto.PlaceRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String addressDetail;
    private String image;
    private String phone;
    private String memo;
    private String longitude;
    private String latitude;

    @Builder
    public Place(Long id, String name, String address, String addressDetail, String image, String phone, String memo,
                 String longitude, String latitude){
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.image = image;
        this.phone = phone;
        this.memo = memo;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
