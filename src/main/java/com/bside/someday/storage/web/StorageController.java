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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.storage.handler.ImageResourceHttpRequestHandler;
import com.bside.someday.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/resource")
public class StorageController {

	private final StorageService storageService;

	private final ImageResourceHttpRequestHandler imageResourceHttpRequestHandler;

	@GetMapping("/{fileName}")
	public void storage(@PathVariable String fileName,
		HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		File file = storageService.getFileByName(fileName);

		request.setAttribute(ImageResourceHttpRequestHandler.ATTRIBUTE_FILE, file);

		imageResourceHttpRequestHandler.handleRequest(request, response);

	}

	@GetMapping("/{fileName}/download")
	public ResponseEntity<Resource> download(@PathVariable String fileName) throws IOException {

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
