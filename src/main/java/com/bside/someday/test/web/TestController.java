package com.bside.someday.test.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bside.someday.common.dto.ResponseDto;

@RestController
public class TestController {

	@GetMapping("/social")
	public ResponseEntity<?> socialSuccess(@RequestParam("id") String id, @RequestParam("socialId") String socialId) {

		Map<String, Object> map = new HashMap<>();

		map.put("id", id);
		map.put("socialId", socialId);

		return ResponseDto.ok(map);
	}
}
