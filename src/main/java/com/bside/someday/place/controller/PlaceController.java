package com.bside.someday.place.controller;

import com.bside.someday.error.exception.InvalidParameterException;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.place.dto.*;
import com.bside.someday.place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Place API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/place")
public class PlaceController {

    private final PlaceService placeService;

    @ApiOperation("장소 등록")
    @PostMapping
    public ResponseEntity addPlace(@Valid @RequestBody PlaceRequestDto placeRequestDto, BindingResult bindingResult
            , @AuthUser UserInfo userInfo) {
        log.info("PlaceController.addPlace {} {}", placeRequestDto, userInfo);

        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        Long placeId = placeService.addPlace(placeRequestDto, userInfo);

        return ResponseEntity.ok().body(new PlaceIdResponseDto(placeId));
    }

    @ApiOperation("장소 조회")
    @GetMapping("/{placeId}")
    public PlaceResponseDto getPlace(@PathVariable Long placeId, @AuthUser UserInfo userInfo) {
        log.info("PlaceController.getPlace {} {}", placeId, userInfo);
        return placeService.getPlace(placeId, userInfo);
    }

    @ApiOperation("장소 목록 조회")
    @GetMapping
    public PlaceListResponseDto getAllPlace(
//            @RequestParam String category,
//            @RequestParam String keyword,
              @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
              @AuthUser UserInfo userInfo) {
        log.info("PlaceController.getAllPlace {}", userInfo);
        return placeService.getAllPlace(pageable, userInfo);
    }

    @ApiOperation("장소 수정")
    @PutMapping("/{placeId}")
    public ResponseEntity modifyPlace(@PathVariable Long placeId
                                    , @Valid @RequestBody PlaceRequestDto placeRequestDto
                                    , BindingResult bindingResult
                                    , @AuthUser UserInfo userInfo) {
        log.info("PlaceController.modifyPlace {} {}", placeId, userInfo);
        if(bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }
        placeService.modifyPlace(placeId, placeRequestDto, userInfo);

        return ResponseEntity.ok().body(new ResponseDto());
    }

    @ApiOperation("장소 삭제")
    @DeleteMapping("/{placeId}")
    public ResponseEntity removePlace(@PathVariable Long placeId, @AuthUser UserInfo userInfo) {
        log.info("PlaceController.removePlace {} {}", placeId, userInfo);
        placeService.removePlace(placeId, userInfo);

        return ResponseEntity.ok().body(new ResponseDto());
    }

}
