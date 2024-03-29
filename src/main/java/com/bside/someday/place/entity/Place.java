package com.bside.someday.place.entity;

import com.bside.someday.user.entity.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@ToString
@EqualsAndHashCode(callSuper = false)
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
    private String roadAddress;
    private String phone;
    private String description;
    @NotNull
    private String longitude;
    @NotNull
    private String latitude;

    private String image;
    private String homepage;

    @Builder
    public Place(Long id, User user, String name, String address, String addressDetail, String roadAddress,
        String phone, String description, String longitude, String latitude, String image, String homepage) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.address = address;
        this.addressDetail = addressDetail;
        this.roadAddress = roadAddress;
        this.phone = phone;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.homepage = homepage;
    }

    public void addUser(User user) {
        this.user = user;
    }

}
