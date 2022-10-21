package com.bside.someday.storage.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bside.someday.storage.repository.StorageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
class StorageServiceTest {

	@Mock
	private StorageRepository storageRepository;

	private StorageService storageService;

	@BeforeEach
	void setup() {
		storageService = new StorageService(storageRepository);
	}

	@Test
	void 랜덤_파일명_생성() {

		//given
		String fileName1 = "asdfnklnsadkf.png";
		String fileName2 = "asdfnklnsadkf.jpeg";
		String fileName3 = "asdfnklns.adkf.txt";
		String fileName4 = "asdfnklns.adkf..jpg";

		//when

		//then
		log.info("변경된 파일명 >> {}", storageService.getUUIDFileName(fileName1));
		log.info("변경된 파일명 >> {}", storageService.getUUIDFileName(fileName2));
		log.info("변경된 파일명 >> {}", storageService.getUUIDFileName(fileName3));
		log.info("변경된 파일명 >> {}", storageService.getUUIDFileName(fileName4));

		assertThat(fileName1.substring(fileName1.lastIndexOf('.'))).isEqualTo(".png");
		assertThat(fileName2.substring(fileName2.lastIndexOf('.'))).isEqualTo(".jpeg");
		assertThat(fileName3.substring(fileName3.lastIndexOf('.'))).isEqualTo(".txt");
		assertThat(fileName4.substring(fileName4.lastIndexOf('.'))).isEqualTo(".jpg");

	}
}
