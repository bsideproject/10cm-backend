package com.bside.someday.common.util;

import static org.springframework.http.HttpHeaders.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.bside.someday.user.dto.SocialType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClientUtil {

    @Value("${kakao-api.logout-uri}")
    private String KAKAO_LOGOUT_URL;

    @Value("${kakao-api.unlink-uri}")
    private String KAKAO_UNLINK_URL;

    @Value("${kakao-api.admin-header}")
    private String KAKAO_ADMIN_HEADER;

    @Value("${kakao-api.admin-key}")
    private String KAKAO_ADMIN_KEY;

    public void requestLogout(String socialId, SocialType socialType) {
        if (!StringUtils.hasText(socialId)) {
            return;
        }

        if (SocialType.KAKAO.equals(socialType)) {
            requestLogoutToKakao(socialId);
        }
    }

    public void requestUnlink(String socialId, SocialType socialType) {
        if (!StringUtils.hasText(socialId)) {
            return;
        }
        if (SocialType.KAKAO.equals(socialType)) {
            requestUnlinkToKakao(socialId);
        }
    }
    public void requestLogoutToKakao(String socialId) {

        log.debug("카카오 로그아웃 요청 >> {}", socialId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set(AUTHORIZATION, KAKAO_ADMIN_HEADER + " " + KAKAO_ADMIN_KEY);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", socialId);

        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);

        HttpEntity<String> response = restTemplate.postForEntity(KAKAO_LOGOUT_URL, requestMessage, String.class);

        log.debug("카카오 로그아웃 결과 >> {}", response);
    }

    public void requestUnlinkToKakao(String socialId) {

        log.debug("카카오 연결끊기 요청 >> {}", socialId);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set(AUTHORIZATION, KAKAO_ADMIN_HEADER + " " + KAKAO_ADMIN_KEY);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", socialId);

        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);

        HttpEntity<String> response = restTemplate.postForEntity(KAKAO_UNLINK_URL, requestMessage, String.class);

        log.debug("카카오 연결끊기 결과 >> {}", response);
    }

}
