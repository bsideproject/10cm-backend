package com.bside.someday.place.controller;

import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.dto.ResponseDto;
import com.bside.someday.place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api("Place API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/place")
public class PlaceController {

    private final PlaceService placeService;

    @ApiOperation("장소 등록")
    @PostMapping
    public ResponseEntity addPlace(@Valid @RequestBody PlaceRequestDto placeRequestDto) {
        log.info("PlaceController.addPlace {}", placeRequestDto);
        placeService.addPlace(placeRequestDto);

        return ResponseEntity.ok().body(new ResponseDto());
    }

    @ApiOperation("장소 조회")
    @GetMapping("/{placeId}")
    public PlaceResponseDto getPlace(@PathVariable Long placeId) {
        log.info("PlaceController.getPlace {}", placeId);
        return placeService.getPlace(placeId);
    }

    @ApiOperation("장소 목록 조회")
    @GetMapping
    public List<PlaceResponseDto> getAllPlace(
//            @RequestParam String category,
//            @RequestParam String keyword,
              @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("PlaceController.getAllPlace");
        return placeService.getAllPlace(pageable);
    }

    @ApiOperation("장소 수정")
    @PutMapping("/{placeId}")
    public ResponseEntity modifyPlace(@PathVariable Long placeId
                                    , @RequestBody PlaceRequestDto placeRequestDto) {
        log.info("PlaceController.modifyPlace {}", placeId);
        placeService.modifyPlace(placeId, placeRequestDto);

        return ResponseEntity.ok().body(new ResponseDto());
    }

    @ApiOperation("장소 삭제")
    @DeleteMapping("/{placeId}")
    public ResponseEntity removePlace(@PathVariable Long placeId) {
        log.info("PlaceController.removePlace {}", placeId);
        placeService.removePlace(placeId);

        return ResponseEntity.ok().body(new ResponseDto());
    }

}
