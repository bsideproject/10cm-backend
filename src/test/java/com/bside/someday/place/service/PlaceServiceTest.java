package com.bside.someday.place.service;

import com.bside.someday.error.exception.NoSuchElementException;
import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.entity.PlaceTag;
import com.bside.someday.place.entity.Tag;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.place.repository.PlaceTagRepository;
import com.bside.someday.place.repository.TagRepository;
import com.bside.someday.user.dto.SocialType;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @Mock
    private PlaceTagRepository placeTagRepository;
    
    @Mock
    private UserRepository userRepository;

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

    private Place place(Long id) {
        String name = "롱플레이";
        String address = "제주특별자치도 제주시 특별자치도";
        String addressDetail = "동복로 44";
        String description = "에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원";
        String longitude = "126.710942515227";
        String latitude = "33.5530690361743";
        String image = "";

        return Place.builder()
                .id(id)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone("")
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .image(image)
                .build();
    }

    private User user(Long id) {
        return User.builder()
                .userId(id)
                .name("홍길동")
                .email("hong@gmail.com")
                .nickname("길동")
                .socialType(SocialType.KAKAO)
                .socialId("KAKAO123")
                .build();
    }

    @DisplayName("정상적인 장소 등록")
    @Test
    void addPlace() {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();
        Long requestUserId = 1L;
        Long requestPlaceId = 1L;
        when(userRepository.findById(requestUserId))
                .thenReturn(Optional.of(user(requestUserId))); // 유저에 대한 Stub

        Place requestPlace = placeRequestDto.toEntity();
        requestPlace.addUser(user(requestUserId));

        when(placeRepository.save(requestPlace))
                .thenReturn(place(requestPlaceId));

        when(tagRepository.findByName("카페"))
                .thenReturn(Optional.of(new Tag(1L, "카페"))); // 카페 태그는 이미 있다고 가정

        when(tagRepository.save(any()))
                .thenReturn(new Tag(2L, "맛집"))
                .thenReturn(new Tag(3L, "제주")); // 두 태그는 없다고 가정

        // when
        Long placeId = placeService.addPlace(placeRequestDto, requestUserId);

        // then
        assertThat(placeId).isEqualTo(1L);

        verify(tagRepository, times(3)).findByName(anyString());
        verify(tagRepository, times(2)).save(any(Tag.class));
        verify(placeTagRepository, times(3)).save(any(PlaceTag.class));
    }

    @DisplayName("유저가 없을 때 장소 등록")
    @Test
    void addPlaceNoUser() {
        // given
        Long requestUserId = 1L;
        when(userRepository.findById(requestUserId))
                .thenReturn(Optional.empty()); // 유저가 empty

        // when
        // then
        assertThrows(UserNotFoundException.class,
                () -> placeService.addPlace(placeRequest(), requestUserId)
        );
    }

    @DisplayName("장소 조회하기")
    @Test
    void getPlace() {
        // given
        Long placeId = 1L;
        Long userId = 1L;
        when(placeRepository.findByIdAndUser_UserId(placeId, userId)).thenReturn(Optional.of(place(placeId)));

        // when
        PlaceResponseDto place = placeService.getPlace(placeId, userId);

        // then
        assertThat(place.getId()).isEqualTo(placeId);
        assertThat(place.getName()).isEqualTo("롱플레이");
    }

    @DisplayName("장소 수정하기")
    @Test
    void modifyPlace() {
        // given
        Long placeId = 1L;
        Long userId = 1L;

        PlaceRequestDto modifyPlaceRequest = PlaceRequestDto.builder()
                .name("이름수정")
                .address("주소수정")
                .addressDetail("상세주소수정")
                .roadAddress("도로명주소수정")
                .phone("번호수정")
                .tag(new String[]{"태그수정1", "태그수정2"})
                .description("설명수정")
                .longitude("123.123")
                .latitude("456.456")
                .image("")
                .build();

        when(placeRepository.findByIdAndUser_UserId(placeId, userId)).thenReturn(Optional.of(place(placeId)));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user(userId)));

        Place requestPlace = modifyPlaceRequest.toEntity(placeId);
        requestPlace.addUser(user(userId));
        when(placeRepository.save(requestPlace))
                .thenReturn(Place.builder()
                    .id(placeId)
                    .name("이름수정")
                    .address("주소수정")
                    .addressDetail("상세주소수정")
                    .roadAddress("도로명주소수정")
                    .phone("번호수정")
                    .description("설명수정")
                    .longitude("123.123")
                    .latitude("456.456")
                    .image("")
                    .build());
        when(tagRepository.save(any()))
                .thenReturn(new Tag(1L, "태그수정1"))
                .thenReturn(new Tag(2L, "태그수정2"));

        // when
        placeService.modifyPlace(placeId, modifyPlaceRequest, userId);

        // then
        // No Error
    }

    @DisplayName("장소 존재할 때 장소 삭제하기")
    @Test
    void removePlace() {
        // given
        Long placeId = 1L;
        Long userId = 1L;
        when(placeRepository.deleteByIdAndUser_UserId(placeId, userId)).thenReturn(1);

        // when
        placeService.removePlace(placeId, userId);

        // then
        // No Error
    }

    @DisplayName("존재하지 않은 장소 삭제하기")
    @Test
    void removePlace_NoExist() {
        // given
        Long placeId = 0L;
        Long userId = 0L;
        when(placeRepository.deleteByIdAndUser_UserId(placeId, userId)).thenReturn(0);

        // when
        // then
        assertThrows(NoSuchElementException.class,
                () -> placeService.removePlace(placeId, userId));
    }

}
