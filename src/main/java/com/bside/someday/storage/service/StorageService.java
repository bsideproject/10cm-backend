package com.bside.someday.storage.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bside.someday.error.exception.oauth.NotAllowAccessException;
import com.bside.someday.error.exception.storage.FileBadRequestException;
import com.bside.someday.error.exception.storage.FileProcessFailException;
import com.bside.someday.storage.entity.ImageData;
import com.bside.someday.error.exception.storage.FileNotFoundException;
import com.bside.someday.error.exception.storage.FileUploadFailException;
import com.bside.someday.storage.repository.StorageRepository;
import com.nimbusds.common.contenttype.ContentType;

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

	public final List<ContentType> FILE_IMAGE_CONTENT_TYPE_LIST = List.of(
		ContentType.IMAGE_JPEG,
		ContentType.IMAGE_PNG,
		ContentType.IMAGE_GIF,
		ContentType.IMAGE_APNG
	);

	@Transactional
	public ImageData uploadFile(MultipartFile file) {

		if (!validateFile(file)) {
			throw new FileBadRequestException();
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

	public boolean validateFile(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			throw new FileBadRequestException("업로드할 이미지가 존재하지 않습니다.");
		}

		String fileExt = Optional.ofNullable(file.getOriginalFilename())
			.filter(f -> f.contains("."))
			.map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".") + 1)).orElse("");

		boolean isValidFileExt = FILE_IMAGE_CONTENT_TYPE_LIST.stream()
			.filter(contentType -> contentType.getSubType().equals(fileExt))
			.anyMatch(contentType -> contentType.getType().equals(file.getContentType()));

		if (!isValidFileExt) {
			log.error("파일 타입 오류로 업로드 실패(contentType:{}, ext:{})", file.getContentType(), fileExt);
			throw new FileBadRequestException("이미지 파일만 업로드 가능합니다.");
		}

		return true;
	}

	@Transactional
	public File getFileByName(String name) {

		File file = new File(findOneByName(name).getFilePath());

		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException();
		}

		return file;
	}

	@Transactional
	public ImageData findOneByName(String name) {
		return storageRepository.findByName(name).orElseThrow(FileNotFoundException::new);
	}

	@Transactional
	public void deleteFile(Long userId, String name) {

		ImageData imageData = findOneByName(name);
		if (!Objects.equals(imageData.getCreatedBy(), userId)) {
			throw new NotAllowAccessException();
		}

		storageRepository.delete(imageData);

		File file = new File(imageData.getFilePath());

		if (!file.exists() || !file.isFile()) {
			throw new FileNotFoundException();
		}

		if (!file.delete()) {
			throw new FileProcessFailException("파일 삭제 중 오류가 발생하였습니다.");
		}
	}

	public String getUUIDFileName(String originalName) {

		if (!StringUtils.hasText(originalName)) {
			return String.valueOf(UUID.randomUUID());
		}

		return UUID.randomUUID() + originalName.substring(originalName.lastIndexOf('.'));
	}
}
