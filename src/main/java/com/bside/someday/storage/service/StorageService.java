package com.bside.someday.storage.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.storage.FileBadRequestException;
import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.error.exception.storage.FileNotFoundException;
import com.bside.someday.error.exception.storage.FileUploadFailException;
import com.bside.someday.storage.repository.StorageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

	private final StorageRepository storageRepository;

	@Value("${file.resources.path:/api/v1/resources}")
	private String FILE_RESOURCE_URL;

	@Value("${file.stored.path:/home}")
	private String FILE_STORED_PATH;

	@Value("${file.resources.domain:https://unzido.site}")
	private String FILE_SERVER_DOMAIN;

	@Transactional
	public ImageData uploadFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new FileBadRequestException("업로드할 이미지가 존재하지 않습니다.");
		}

		String name = getUUIDFileName(file.getOriginalFilename());

		String filePath = FILE_STORED_PATH + "/" + name;

		// TODO: 이미지 압축, 이미지 크기 제한, 확장자 제한

		// 이미지 업로드
		try (
			InputStream inputStream = file.getInputStream()
		) {
			Files.copy(inputStream, Paths.get(filePath));
		} catch (IOException e) {
			throw new FileUploadFailException();
		}

		return storageRepository.save(ImageData.builder()
			.name(name)
			.originalName(file.getOriginalFilename())
			.type(file.getContentType())
			.filePath(filePath)
			.size(file.getSize())
			.url(FILE_SERVER_DOMAIN + FILE_RESOURCE_URL + "/" + name)
			.build());
	}

	public File getFileByName(String name) {

		File file = new File(FILE_STORED_PATH + "/" + name);

		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException();
		}

		return file;
	}

	public ImageData findOneByName(String name) {
		return storageRepository.findByName(name).orElseThrow(FileNotFoundException::new);
	}

	@Transactional
	public void deleteFile(Long id, String name) {

		ImageData imageData = findOneByName(name);
		if (!Objects.equals(imageData.getCreatedBy(), id)) {
			throw new NotAllowAccessException();
		}

		storageRepository.delete(imageData);

		File file = new File(FILE_STORED_PATH + "/" + name);

		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException();
		}

		if (!file.delete()) {
			// TODO: 삭제 실패시 예외처리
		}
	}

	public String getUUIDFileName(String originalName) {

		if (!StringUtils.hasText(originalName)) {
			return String.valueOf(UUID.randomUUID());
		}

		return UUID.randomUUID() + originalName.substring(originalName.lastIndexOf('.'));
	}
}
