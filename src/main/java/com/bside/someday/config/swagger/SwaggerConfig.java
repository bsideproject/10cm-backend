package com.bside.someday.config.swagger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import lombok.Data;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final String API_TITLE = "언젠가 와있을 지도";
	private static final String API_VERSION = "0.0.1";
	private static final String API_DESCRIPTION = "언젠가 와있을 지도 API";

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
		Arrays.asList("application/json")
	);

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return List.of(new SecurityReference("JWT", authorizationScopes));
	}

	private ResolvedType typeResolver(Class<?> clazz) {
		return new TypeResolver().resolve(clazz);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.enable(true)
			.useDefaultResponseMessages(false)
			.alternateTypeRules(
				AlternateTypeRules.newRule(typeResolver(Pageable.class), typeResolver(SwaggerPageable.class)))
			.ignoredParameterTypes(
				WebSession.class,
				ServerHttpRequest.class,
				ServerHttpResponse.class,
				ServerWebExchange.class
			)
			.apiInfo(getApiInfo())
			.produces(DEFAULT_PRODUCES_AND_CONSUMES)
			.consumes(DEFAULT_PRODUCES_AND_CONSUMES)
			.securityContexts(List.of(securityContext()))
			.securitySchemes(List.of(new ApiKey("JWT", "Authorization", "header")))
			.select()
			.paths(PathSelectors.any())
			.paths(PathSelectors.ant("/api/**"))
			.build();
	}

	// API 정보
	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder()
			.title(API_TITLE)
			.description(API_DESCRIPTION)
			.version(API_VERSION)
			.build();
	}

	@Data
	public static class SwaggerPageable {
		private Integer page;
		private Integer size;
	}

}
