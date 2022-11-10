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
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;
    private String address;
    private String addressDetail;
    private String phone;
    private String description;
    @NotNull
    private String longitude;
    @NotNull
    private String latitude;

    @Builder
    public Place(Long id, String name, String address, String addressDetail, String phone, String description,
                 String longitude, String latitude){
        this.id = id;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phone = phone;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

}
