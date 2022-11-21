package com.goryaninaa.web.HttpServer.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.goryaninaa.web.HttpServer.requesthandler.HttpResponseCode;

class HttpResponseTest {
	
	@Test
	void httpResponseShouldCorrectlyFormWith1Arg() {
		HttpResponse httpResponse = new HttpResponse(HttpResponseCode.NOTFOUND);
		
		String fact = httpResponse.getResponseString();
		String expected = "HTTP/1.1 404 Not Found\n";
		
		assertEquals(expected, fact);
	}

	@Test
	void httpResponseShouldCorrectlyFormWith2Arg() {
		String body = "<p>Hello!</p>";
		
		HttpResponse httpResponse = new HttpResponse(HttpResponseCode.NOTFOUND, body);
		
		Pattern pattern = Pattern.compile("Date:.*\\n");
		
		String factWithDate = httpResponse.getResponseString();
		Matcher matcher = pattern.matcher(factWithDate);
		matcher.find();
		
		String factWithoutDate = factWithDate.substring(0, matcher.start()) + factWithDate.substring(matcher.end());
		
		String expected = "HTTP/1.1 404 Not Found\n"
				+ "Server: RagingServer\n"
				+ "Connection: close\n"
				+ "Content-Type: text/html; charset=utf-8\n"
				+ "Content-Length: 13\n"
				+ "\n"
				+ "<p>Hello!</p>";
		
		assertEquals(expected, factWithoutDate);
	}
	
	@Test
	void httpResponseShouldCorrectlyFormWithJson() {
		PersonStub person = new PersonStub("Alex");
		
		HttpResponse httpResponse = new HttpResponse(HttpResponseCode.OK, person);
		
		Pattern pattern = Pattern.compile("Date:.*\\n");
		
		String factWithDate = httpResponse.getResponseString();
		Matcher matcher = pattern.matcher(factWithDate);
		matcher.find();
		
		String factWithoutDate = factWithDate.substring(0, matcher.start()) + factWithDate.substring(matcher.end());
		
		String expected = "HTTP/1.1 200 OK\n"
				+ "Server: RagingServer\n"
				+ "Connection: close\n"
				+ "Content-Type: application/json\n"
				+ "Content-Length: 16\n"
				+ "\n"
				+ "{\"name\": \"Alex\"}";
		
		assertEquals(expected, factWithoutDate);
	}
}
