package com.bside.someday.storage.handler;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
public class ImageResourceHttpRequestHandler extends ResourceHttpRequestHandler {

	public static final String ATTRIBUTE_FILE = "DOWNLOAD";

	@Override
	protected Resource getResource(HttpServletRequest request) {
		return new FileSystemResource((File) request.getAttribute(ATTRIBUTE_FILE));
	}
}
