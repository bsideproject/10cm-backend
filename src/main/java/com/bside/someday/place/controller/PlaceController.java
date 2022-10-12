package com.bside.someday.place.controller;

import com.bside.someday.error.dto.ErrorDto;
import com.bside.someday.error.dto.ErrorType;
import com.bside.someday.place.dto.PlaceRequestDto;
import com.bside.someday.place.dto.PlaceResponseDto;
import com.bside.someday.place.dto.ResponseDto;
import com.bside.someday.place.entity.Place;
import com.bside.someday.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping("/api/v1/place")
    public ResponseEntity addPlace(@RequestBody PlaceRequestDto placeRequestDto) {
        log.info("PlaceController.addPlace {}", placeRequestDto);
        placeService.addPlace(placeRequestDto);

        return ResponseEntity.ok().body(new ResponseDto());
    }

    @GetMapping("/api/v1/place/{placeId}")
    public PlaceResponseDto getPlace(@PathVariable Long placeId) {
        log.info("PlaceController.getPlace {}", placeId);
        return placeService.getPlace(placeId);
    }

    @GetMapping("/api/v1/place")
    public List<PlaceResponseDto> getAllPlace() {
        log.info("PlaceController.getAllPlace");
        return placeService.getAllPlace();
    }

    @PutMapping("/api/v1/place/{placeId}")
    public ResponseEntity modifyPlace(@PathVariable Long placeId
                                    , @RequestBody PlaceRequestDto placeRequestDto) {
        log.info("PlaceController.modifyPlace {}", placeId);
        placeService.modifyPlace(placeId, placeRequestDto);

        return ResponseEntity.ok().body(new ResponseDto());
    }

    @DeleteMapping("/api/v1/place/{placeId}")
    public ResponseEntity removePlace(@PathVariable Long placeId) {
        log.info("PlaceController.removePlace {}", placeId);
        placeService.removePlace(placeId);

        return ResponseEntity.ok().body(new ResponseDto());
    }

}
