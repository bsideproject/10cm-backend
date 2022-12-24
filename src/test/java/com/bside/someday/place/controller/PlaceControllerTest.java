package com.bside.someday.place.controller;

import com.bside.someday.place.dto.*;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.service.PlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PlaceControllerTest {

    @InjectMocks
    private PlaceController placeController;

    @Mock
    private PlaceService placeService;

    private MockMvc mvc;

    @BeforeEach
    private void init() {
        mvc = MockMvcBuilders.standaloneSetup(placeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

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

    private PlaceResponseDto placeResponse() {
        String name = "롱플레이";
        String address = "제주특별자치도 제주시 특별자치도";
        String addressDetail = "동복로 44";
        String[] tag = {"카페","맛집","제주"};
        String description = "에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원";
        String longitude = "126.710942515227";
        String latitude = "33.5530690361743";
        String image = "";

        Place place = Place.builder()
                .id(1L)
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone("")
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .image(image)
                .build();

        return PlaceResponseDto.builder()
                .place(place)
                .tag(tag)
                .build();
    }

    private PlaceListResponseDto placeListResponseDto() {
        List<PlaceResponseDto> list = new ArrayList<>();
        list.add(PlaceResponseDto.builder()
                .place(Place.builder()
                        .id(1L)
                        .name("롱플레이")
                        .address("제주특별자치도 제주시 특별자치도")
                        .addressDetail("동복로 44")
                        .phone("")
                        .description("에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원")
                        .longitude("126.710942515227")
                        .latitude("33.5530690361743")
                        .image("")
                        .build())
                .tag(new String[]{"카페", "맛집", "제주"})
                .build());

        list.add(PlaceResponseDto.builder()
                .place(Place.builder()
                        .id(2L)
                        .name("모던기와")
                        .address("경기 구리시 아차산로 37")
                        .addressDetail("")
                        .phone("")
                        .description("아메리카노5,500\n" +
                                "카페라떼6,000\n" +
                                "녹차라떼6,000\n" +
                                "레몬티6,500\n" +
                                "얼그레이 라벤더티5,600\n" +
                                "유자차6,000\n" +
                                "오레오쿠키 스무디7,000")
                        .longitude("127.11388451442271")
                        .latitude("37.561689587924924")
                        .image("")
                        .build())
                .tag(new String[]{"데이트코스", "야외카페", "한옥카페", "경기지역화폐"})
                .build());

        return PlaceListResponseDto.builder()
                .placeList(list)
                .count(2)
                .build();
    }

    @DisplayName("장소등록하기")
    @Test
    void Place_등록하기() throws Exception {
        // given
        PlaceRequestDto placeRequestDto = placeRequest();
        when(placeService.addPlace(placeRequestDto, null))
                .thenReturn(1L);

        // when
        ResultActions resultActions = mvc.perform(
                post("/api/v1/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(placeRequestDto))
        );

        // then
        MvcResult result = resultActions
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PlaceIdResponseDto responseDto = new ObjectMapper()
                .readValue(result.getResponse().getContentAsString(), PlaceIdResponseDto.class);
        assertThat(responseDto.getId()).isEqualTo(1L);
    }

    @DisplayName("장소조회하기")
    @Test
    void Place_조회하기() throws Exception {
        // given
        Long placeId = 1L;
        when(placeService.getPlace(placeId, null))
                .thenReturn(placeResponse());

        // when
        ResultActions resultActions = mvc.perform(
                get("/api/v1/place/" + placeId)
        );

        // then
        MvcResult result = resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("롱플레이"))
                .andDo(print())
                .andReturn();
        PlaceResponseDto responseDto = new ObjectMapper()
                .readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), PlaceResponseDto.class);
        assertThat(responseDto.getName()).isEqualTo("롱플레이");
    }

    @DisplayName("장소목록조회하기")
    @Test
    void Place_목록조회하기() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 8, Sort.Direction.DESC, "createdDate");
        String tag = null; // 태그 검색 없이 모든 목록 조회
        when(placeService.getAllPlace(pageable, null, tag))
                .thenReturn(placeListResponseDto());

        // when
        ResultActions resultActions = mvc.perform(
                get("/api/v1/place")
        );

        // then
        String placeListAsString = resultActions.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        PlaceListResponseDto placeListResponseDto = new ObjectMapper()
                .readValue(placeListAsString, PlaceListResponseDto.class);
        assertThat(placeListResponseDto.getPlaceList().size()).isEqualTo(2);
    }

    @DisplayName("장소수정하기")
    @Test
    void Place_수정하기() throws Exception {
        // given
        Long placeId = 1L;
        PlaceRequestDto placeRequestDto = placeRequest();

        // when
        ResultActions resultActions = mvc.perform(
                put("/api/v1/place/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(placeRequestDto))
        );

        // then
        String responseDtoAsString = resultActions.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResponseDto responseDto = new ObjectMapper().readValue(responseDtoAsString, ResponseDto.class);

        assertThat(responseDto.getMessage()).isEqualTo("success");
        assertThat(responseDto.getCode()).isEqualTo("SUC01");
        assertThat(responseDto.getStatus()).isEqualTo(200);
    }

    @DisplayName("장소삭제하기")
    @Test
    void Place_삭제하기() throws Exception {
        // given
        Long placeId = 1L;

        // when
        ResultActions resultActions = mvc.perform(
                delete("/api/v1/place/" + placeId)
        );

        // then
        String contentAsString = resultActions.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResponseDto responseDto = new ObjectMapper().readValue(contentAsString, ResponseDto.class);

        assertThat(responseDto.getMessage()).isEqualTo("success");
        assertThat(responseDto.getCode()).isEqualTo("SUC01");
        assertThat(responseDto.getStatus()).isEqualTo(200);
    }

}
