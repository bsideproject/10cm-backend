package com.bside.someday.place.service;

import com.bside.someday.error.exception.NoSuchElementException;
import com.bside.someday.error.exception.oauth.UserNotFoundException;
import com.bside.someday.place.dto.PlaceListResponseDto;
import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.entity.PlaceTag;
import com.bside.someday.place.entity.Tag;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.place.repository.PlaceTagRepository;
import com.bside.someday.place.repository.TagRepository;
import com.bside.someday.user.entity.User;
import com.bside.someday.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final PlaceTagRepository placeTagRepository;

    private final UserRepository userRepository;

    @Transactional
    public Long addPlace(PlaceRequestDto placeRequestDto, Long userId) {

        Place place = placeRequestDto.toEntity();
        place.addUser(getUser(userId));
        //place 추가
        Long placeId = placeRepository.save(place).getId();

        // tag 추가
        addTag(placeRequestDto, placeId);
        return placeId;
    }

    public PlaceResponseDto getPlace(Long placeId, Long userId) {
        Place place = placeRepository.findByIdAndUser_UserId(placeId, userId)
                .orElseThrow(() -> new NoSuchElementException());
        List<String> tagList = getTagList(placeId);

        return PlaceResponseDto.builder()
                .place(place)
                .tag(tagList.toArray(new String[0]))
                .build();
    }


    @Transactional
    public void removePlace(Long placeId, Long userId) {
        //place tag 삭제
        placeTagRepository.deleteByPlaceId(placeId);
        //place 삭제
        int count = placeRepository.deleteByIdAndUser_UserId(placeId, userId);

        if(count < 1) {
            throw new NoSuchElementException();
        }
    }

    public PlaceListResponseDto getAllPlace(Pageable pageable, Long userId, String tag) {
        long count = 0L;
        Page<Place> placePage = null;
        if(tag == null) {
            count = placeRepository.countByUser_UserId(userId);
            placePage = placeRepository.findAllByUser_UserId(pageable, userId);
        }else {
            List<Long> placeIdList = placeRepository.findByTagContains(userId, tag);
            placePage = placeRepository.findByIdIn(pageable, placeIdList);
            count = placeIdList.size();
        }

        List<PlaceResponseDto> placeList = new ArrayList<>();
        for(Place place : placePage){
            List<String> tagList = getTagList(place.getId());
            placeList.add(PlaceResponseDto.builder()
                    .place(place)
                    .tag(tagList.toArray(new String[0]))
                    .build());
        }

        return PlaceListResponseDto.builder()
                .placeList(placeList)
                .count(count)
                .build();
    }

    @Transactional
    public void modifyPlace(Long placeId, PlaceRequestDto placeRequestDto, Long userId) {
        // 해당 place id의 장소가 있는지 체크
        placeRepository.findByIdAndUser_UserId(placeId, userId)
                .orElseThrow(() -> new NoSuchElementException());

        Place requestPlace = placeRequestDto.toEntity(placeId);
        requestPlace.addUser(getUser(userId));
        // 해당 place id로 내용 수정
        Place place = placeRepository.save(requestPlace);
        log.info("수정한 장소 {}", place);
        // tag 수정
        placeTagRepository.deleteByPlaceId(placeId); // 매핑된 place tag 모두 삭제
        // tag 추가
        addTag(placeRequestDto, placeId);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());// userInfo로 user를 구함..
    }

    private List<String> getTagList(Long placeId) {
        List<PlaceTag> placeTagList = placeTagRepository.findByPlaceId(placeId);
        List<String> tagList = new ArrayList<>();
        for(PlaceTag placeTag : placeTagList) {
            Tag tag = tagRepository.findById(placeTag.getTagId()).get();
            tagList.add(tag.getName());
        }
        return tagList;
    }

    private void addTag(PlaceRequestDto placeRequestDto, Long placeId) {
        // 태그가 이미 존재하는지 체크
        String[] tagArr = placeRequestDto.getTag();
        if(tagArr != null) {
            for (String tagName : tagArr) {
                Optional<Tag> tag = tagRepository.findByName(tagName);
                log.info("tagName exists check {} {}", tagName, tag.isPresent());
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
    }
}
