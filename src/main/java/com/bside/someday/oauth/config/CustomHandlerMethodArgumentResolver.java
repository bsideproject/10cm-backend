package com.bside.someday.oauth.config;

import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.bside.someday.oauth.dto.UserInfo;
import com.bside.someday.user.entity.Role;

@Component
public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		boolean result = false;

		AuthUser authUser = parameter.getParameterAnnotation(AuthUser.class);

		// 메서드 파라미터 중 @AuthUser
		if (authUser != null) {
			result = parameter.getParameterType().equals(UserInfo.class);
		}

		return result;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

		Object principal = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 인증정보 없는 경우 -> 등록
		if (authentication != null) {
			principal = authentication.getPrincipal();
		}

		// 인증정보 null
		if (principal == null || principal instanceof String) {
			return null;
		}

		return new UserInfo(Long.valueOf(String.valueOf(authentication.getPrincipal())),
			authentication.getAuthorities()
				.stream()
				.map(authority -> Role.valueOf(authority.getAuthority()))
				.collect(Collectors.toList()));
	}

}
