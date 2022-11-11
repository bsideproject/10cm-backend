package com.bside.someday.place.controller;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.entity.PlaceTag;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.place.repository.PlaceTagRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
class PlaceControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceTagRepository placeTagRepository;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    void Place_등록하기() throws Exception {

        String name = "롱플레이";
        String address = "제주특별자치도 제주시 특별자치도";
        String addressDetail = "동복로 44";
        String[] tag = {"카페","맛집","제주"};
        String description = "에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원";
        String longitude = "126.710942515227";
        String latitude = "33.5530690361743";

        PlaceRequestDto placeRequestDto = PlaceRequestDto.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone("")
                .tag(tag)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .build();

        mvc.perform(
                post("/api/v1/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(placeRequestDto))
        ).andExpect(status().isOk());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList.get(0).getName()).isEqualTo(name);
        assertThat(placeList.get(0).getAddress()).isEqualTo(address);
        assertThat(placeList.get(0).getAddressDetail()).isEqualTo(addressDetail);
        assertThat(placeList.get(0).getDescription()).isEqualTo(description);
        assertThat(placeList.get(0).getLongitude()).isEqualTo(longitude);
        assertThat(placeList.get(0).getLatitude()).isEqualTo(latitude);

        List<PlaceTag> placeTagList = placeTagRepository.findByPlaceId(placeList.get(0).getId());
        assertThat(placeTagList.size()).isEqualTo(tag.length);
    }

    @Test
    @WithMockUser(roles = "USER")
    void Place_조회하기() throws Exception {

        String name = "롱플레이";
        String address = "제주특별자치도 제주시 특별자치도";
        String addressDetail = "동복로 44";
        String[] tag = {"카페","맛집","제주"};
        String description = "에스프레소 4,000원\n에스프레소 폰파냐 4,500원\n아메리카노 5,500원\n카페 라떼 : 6,000원\n바닐라 라테 6,500원\n카푸치노 6,000원\n플랫화이트 6,000원\n초코 6,500원\n콜라 4,500원";
        String longitude = "126.710942515227";
        String latitude = "33.5530690361743";

        PlaceRequestDto placeRequestDto = PlaceRequestDto.builder()
                .name(name)
                .address(address)
                .addressDetail(addressDetail)
                .phone("")
                .tag(tag)
                .description(description)
                .longitude(longitude)
                .latitude(latitude)
                .build();

        mvc.perform(
                post("/api/v1/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(placeRequestDto))
        ).andExpect(status().isOk());

        mvc.perform(get("/api/v1/place/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));

    }

}
