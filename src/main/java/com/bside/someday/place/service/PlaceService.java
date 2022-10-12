package com.bside.someday.place.service;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.entity.PlaceTag;
import com.bside.someday.place.entity.Tag;
import com.bside.someday.place.repository.PlaceRepository;
import com.bside.someday.place.repository.PlaceTagRepository;
import com.bside.someday.place.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public void addPlace(PlaceRequestDto placeRequestDto) {
        //place 추가
        Long placeId = placeRepository.save(placeRequestDto.toEntity()).getId();

        // tag 추가
        addTag(placeRequestDto, placeId);
    }

    public PlaceResponseDto getPlace(Long placeId) {
        Place place = placeRepository.findById(placeId).get();
        List<String> tagList = getTagList(placeId);

        return PlaceResponseDto.builder()
                .place(place)
                .tag(tagList.toArray(new String[0]))
                .build();
    }


    @Transactional
    public void removePlace(Long placeId) {
        //place tag 삭제
        placeTagRepository.deleteByPlaceId(placeId);
        //place 삭제
        placeRepository.deleteById(placeId);
    }

    public List<PlaceResponseDto> getAllPlace() {
        List<Place> placeList = placeRepository.findAll();
        List<PlaceResponseDto> result = new ArrayList<>();
        for(Place place : placeList){
            List<String> tagList = getTagList(place.getId());
            result.add(PlaceResponseDto.builder()
                    .place(place)
                    .tag(tagList.toArray(new String[0]))
                    .build());
        }
        return result;
    }

    @Transactional
    public void modifyPlace(Long placeId, PlaceRequestDto placeRequestDto) {
        // 해당 place id로 내용 수정
        placeRepository.save(placeRequestDto.toEntity(placeId));
        // tag 수정
        placeTagRepository.deleteByPlaceId(placeId); // 매핑된 place tag 모두 삭제
        // tag 추가
        addTag(placeRequestDto, placeId);
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
        for(String tagName : tagArr) {
            Optional<Tag> tag = tagRepository.findByName(tagName);
            log.info("tagName exists check {} {}", tagName, tag.isPresent());
            Long tagId = 0L;
            if(!tag.isPresent()) { // 존재하지 않는다면
                //tag 추가
                tagId = tagRepository.save(new Tag(tagName)).getId();

            }else { // 존재한다면
                tagId = tag.get().getId();
            }
            //place_tag 추가
            placeTagRepository.save(new PlaceTag(placeId, tagId));
        }
    }
}
