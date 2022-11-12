package com.goryaninaa.web.HttpServer.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {
    private final String request;
    private String method;
    private String mapping;
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(String request) {
        this.request = request;
        defineMethod();
        defineMapping();
        defineParameters();
        defineHeaders();
        defineBody();
    }

	public String getMapping() {
		return mapping;
	}

	public String getMethod() {
		return method;
	}

	public Optional<String> getConttrollerMapping(int length) {
		if (mapping.length() >= length) {
			Optional<String> controllerMapping = Optional.ofNullable(mapping.substring(0, length));
			return controllerMapping;
		} else {
			return Optional.empty();
		}
	}

	public Optional<String> getParameterByName(String name) {
		return Optional.ofNullable(parameters.get(name));
	}

	public Optional<String> getHeaderByName(String name) {
		return Optional.ofNullable(headers.get(name));
	}

	public Optional<String> getBody() {
		return Optional.ofNullable(body);
	}

	private void defineMethod() {
		Pattern pattern = Pattern.compile("(GET|POST|PUT|PATCH|DELETE)\\s");
		Matcher matcher = pattern.matcher(request);
		
		if (matcher.find()) {
			this.method = request.substring(0, matcher.end()).trim();
		} else {
			throw new IllegalArgumentException("Unsupported http request method");
		}
	}

	private void defineMapping() {
		Pattern patternWParams = Pattern.compile("\\/.*?\\?");
		Matcher matcherWParams = patternWParams.matcher(request);
		
		Pattern patternWNoParams = Pattern.compile("\\/.*?\\s");
		Matcher matcherWNoParams = patternWNoParams.matcher(request);
		
		if (matcherWParams.find()) {
			this.mapping = request.substring(matcherWParams.start(), matcherWParams.end() - 1).trim();
		} else if (matcherWNoParams.find()){
			this.mapping = request.substring(matcherWNoParams.start(), matcherWNoParams.end()).trim();
		} else {
			throw new IllegalArgumentException("Missing mapping in http request");
		}
	}

	private void defineParameters() {
		Optional<String> parametersString = Optional.ofNullable(cutParametersString());
		
		if (parametersString.isPresent()) {
			String[] lines = parametersString.get().split("&");

			for (String line : lines) {
				parameters.put(line.split("=")[0], line.split("=")[1]);
			}
		}
	}

	private String cutParametersString() {
		Pattern pattern = Pattern.compile("\\?.+?\\s");
		Matcher matcher = pattern.matcher(request);
		
		if (matcher.find()) {
			return request.substring(matcher.start() + 1, matcher.end()).trim();
		} else {
			return null;
		}
	}

	private void defineHeaders() {
		Optional<String> headersString = Optional.ofNullable(cutHeadersString());
		
		if (headersString.isPresent()) {
			String[] lines = headersString.get().split("\\n");

			for (String line : lines) {
				headers.put(line.split(": ")[0].trim(), line.split(": ")[1].trim());
			}
		}
	}
	
	private String cutHeadersString() {
		Pattern pattern = Pattern.compile("(?s)\\n.*\\n\\n");
		Matcher matcher = pattern.matcher(request);
		
		if (matcher.find()) {
			return request.substring(matcher.start(), matcher.end()).trim();
		} else {
			return null;
		}
	}
	
	private void defineBody() {
		Pattern pattern = Pattern.compile("\\n\\n");
		Matcher matcher = pattern.matcher(request);
		
		if (matcher.find()) {
			this.body = request.substring(matcher.end()).trim();
		}
	}
}
