package com.bside.someday.place.entity;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Place extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @NotNull(message = "name 필드는 필수입니다.")
    private String name;
    private String address;
    private String addressDetail;
    private String phone;
    private String description;
    @NotNull
    private String longitude;
    @NotNull
    private String latitude;

    private String image;

    @Builder
    public Place(Long id, User user, String name, String address, String addressDetail, String phone, String description,
                 String longitude, String latitude, String image){
        this.id = id;
        this.user = user;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phone = phone;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
    }

}
