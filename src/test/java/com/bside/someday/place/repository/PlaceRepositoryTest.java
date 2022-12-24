package com.bside.someday.place.repository;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.entity.PlaceTag;
import com.bside.someday.place.entity.Tag;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class PlaceRepositoryTest {

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    PlaceTagRepository placeTagRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    UserRepository userRepository;


    private PlaceRequestDto placeRequest() {
        String name = "롱플레이";
        String address = "제주특별자치도 제주시 특별자치도";
        String addressDetail = "동복로 44";
        String[] tag = {"카페","맛집","제주"};
        String description = "에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원";
        String longitude = "126.710942515227";
        String latitude = "33.5530690361743";
        String image = "";

        return PlaceRequestDto.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone("")
                .tag(tag)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .image(image)
                .build();
    }

    private User user() {
        return User.builder()
                .name("홍길동")
                .socialType(SocialType.KAKAO)
                .socialId("KAKAO123")
                .build();
    }

    @DisplayName("장소 등록하기")
    @Transactional
    @Test
    void addPlace() {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();

        // when
        Place place = placeRepository.save(placeRequestDto.toEntity());

        // then
        assertThat(place.getId()).isEqualTo(1L);
        assertThat(place.getName()).isEqualTo("롱플레이");
        assertThat(place.getAddress()).isEqualTo("제주특별자치도 제주시 특별자치도");
        assertThat(place.getAddressDetail()).isEqualTo("동복로 44");
        assertThat(place.getDescription()).isEqualTo("에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원");
        assertThat(place.getLongitude()).isEqualTo("126.710942515227");
        assertThat(place.getLatitude()).isEqualTo("33.5530690361743");
    }

    @DisplayName("태그 등록하기(존재하는 태그 없음)")
    @Transactional
    @Test
    void addTag() {
        // given
        Long placeId = 1L;
        PlaceRequestDto placeRequestDto = placeRequest();
        String[] tagArr = placeRequestDto.getTag();

        // when
        if(tagArr != null) {
            for (String tagName : tagArr) {
                Optional<Tag> tag = tagRepository.findByName(tagName);
                Long tagId = 0L;
                if (!tag.isPresent()) { // 존재하지 않는다면
                    //tag 추가
                    tagId = tagRepository.save(new Tag(tagName)).getId();
                } else { // 존재한다면
                    tagId = tag.get().getId();
                }
                //place_tag 추가
                placeTagRepository.save(new PlaceTag(placeId, tagId));
            }
        }

        // then
        assertThat(tagRepository.findAll().size()).isEqualTo(3);
    }

    @DisplayName("장소 조회하기")
    @Transactional
    @Test
    void getPlace() {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();

        User user = userRepository.save(user());
        Long userId = user.getUserId();
        user = userRepository.findById(userId).get();

        placeRequestDto.addUser(user);
        Place savePlace = placeRepository.save(placeRequestDto.toEntity());
        Long placeId = savePlace.getId();

        // when
        Place place = placeRepository.findByIdAndUser_UserId(placeId, userId).get();

        // then
        assertThat(place.getId()).isEqualTo(placeId);
    }

    @DisplayName("장소 수정하기")
    @Transactional
    @Test
    void modifyPlace() {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();

        User user = userRepository.save(user());
        Long userId = user.getUserId();
        user = userRepository.findById(userId).get();

        placeRequestDto.addUser(user);
        Place savePlace = placeRepository.save(placeRequestDto.toEntity()); //기존 장소 등록
        Long placeId = savePlace.getId();

        // 수정할 장소
        PlaceRequestDto modifyPlaceRequest = PlaceRequestDto.builder()
                .name("이름수정")
                .address("주소수정")
                .addressDetail("상세주소수정")
                .phone("번호수정")
                .tag(new String[]{"태그수정1", "태그수정2"})
                .description("설명수정")
                .longitude("123.123")
                .latitude("456.456")
                .image("")
                .build();

        // when
        Place place = placeRepository.save(modifyPlaceRequest.toEntity(placeId)); // 수정

        // then
        assertThat(place.getId()).isEqualTo(placeId);
        assertThat(place.getName()).isEqualTo("이름수정");
        assertThat(place.getAddress()).isEqualTo("주소수정");
        assertThat(place.getAddressDetail()).isEqualTo("상세주소수정");
        assertThat(place.getPhone()).isEqualTo("번호수정");
        assertThat(place.getDescription()).isEqualTo("설명수정");
        assertThat(place.getLongitude()).isEqualTo("123.123");
        assertThat(place.getLatitude()).isEqualTo("456.456");

    }

    @DisplayName("장소 삭제하기(존재하는 장소일 때)")
    @Transactional
    @Test
    void removePlace() {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();

        User user = userRepository.save(user());
        Long userId = user.getUserId();
        user = userRepository.findById(userId).get();

        placeRequestDto.addUser(user);
        Place savePlace = placeRepository.save(placeRequestDto.toEntity());
        Long placeId = savePlace.getId();

        // when
        int count = placeRepository.deleteByIdAndUser_UserId(placeId, userId);

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("장소 삭제하기(존재하지 않은 장소일 때)")
    @Transactional
    @Test
    void removePlace_NoExist() {
        // given
        Long userId = 0L;
        Long placeId = 0L;

        // when
        int count = placeRepository.deleteByIdAndUser_UserId(placeId, userId);

        // then
        assertThat(count).isEqualTo(0);
    }
}
