package com.bside.someday.oauth.web;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = "spring.security.oauth2.client.registration.kakao.client-id=test-id")
public class SecurityApplicationTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private String basePath;

	@BeforeEach
	public void setup() {
		basePath = "http://localhost:" + port + "/";
	}

	@Test
	public void authorizationEndpointRedirectTest() {
		ResponseEntity response = restTemplate.getForEntity(basePath + "oauth2/authorization/kakao", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		assertThat(response.getBody()).isNull();

		URI redirectLocation = response.getHeaders().getLocation();
		assertThat(redirectLocation).isNotNull();
		assertThat(redirectLocation.getHost()).isEqualTo("kauth.kakao.com");
		assertThat(redirectLocation.getPath()).isEqualTo("/oauth/authorize");

		String redirectionQuery = redirectLocation.getQuery();
		assertThat(redirectionQuery.contains("response_type=code"));
		assertThat(redirectionQuery.contains("client_id=test-id"));
		assertThat(redirectionQuery.contains("scope=read:user"));

	}
}
