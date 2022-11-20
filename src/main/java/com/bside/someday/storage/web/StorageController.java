 package com.bside.someday.storage.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.common.dto.ResponseDto;
import com.bside.someday.error.exception.oauth.UnAuthorizedException;
import com.bside.someday.oauth.config.AuthUser;
import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.storage.dto.response.ImageResponseDto;
import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.storage.handler.ImageResourceHttpRequestHandler;
import com.bside.someday.storage.service.StorageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Storage API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/resources")
public class StorageController {

	private final StorageService storageService;

	private final ImageResourceHttpRequestHandler imageResourceHttpRequestHandler;

	@ApiOperation("이미지 조회")
	@GetMapping("/{fileName}")
	public void storage(@PathVariable String fileName,
		HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		File file = storageService.getFileByName(fileName);

		request.setAttribute(ImageResourceHttpRequestHandler.ATTRIBUTE_FILE, file);

		imageResourceHttpRequestHandler.handleRequest(request, response);
	}

	@ApiOperation("이미지 업로드")
	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<ImageResponseDto> upload(@AuthUser UserInfo userInfo,
		@RequestPart(name = "file", required = false) MultipartFile multipartFile) {

		if (userInfo == null) {
			throw new UnAuthorizedException();
		}

		return ResponseDto.created(new ImageResponseDto(storageService.uploadFile(multipartFile)));
	}

	@ApiOperation("이미지 다운로드 조회")
	@GetMapping("/{fileName}/download")
	public ResponseEntity<Resource> download(@PathVariable String fileName) throws
		IOException {

		ImageData imageData = storageService.findOneByName(fileName);

		Path filePath = Paths.get(imageData.getFilePath());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentDisposition(ContentDisposition.builder("attachment")
			.filename(imageData.getOriginalName(), StandardCharsets.UTF_8)
			.build());

		httpHeaders.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));
		Resource resource = new InputStreamResource(Files.newInputStream(filePath));

		return new ResponseEntity<>(resource, httpHeaders, HttpStatus.OK);
	}

}
