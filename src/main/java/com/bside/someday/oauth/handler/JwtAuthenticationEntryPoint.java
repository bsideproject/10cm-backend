package com.bside.someday.oauth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONObject;

import com.bside.someday.error.exception.BusinessException;
import com.bside.someday.error.exception.oauth.UnAuthorizedException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		log.error("JwtAuthenticationEntryPoint.commence >> {}", authException.getMessage());
		setResponse(response, new UnAuthorizedException());
	}

	private void setResponse(HttpServletResponse response, BusinessException exception) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		JSONObject responseJson = new JSONObject();
		responseJson.put("message", exception.getErrorType().getMessage());
		responseJson.put("code", exception.getErrorType().getCode());

		response.getWriter().print(responseJson);
	}
}
