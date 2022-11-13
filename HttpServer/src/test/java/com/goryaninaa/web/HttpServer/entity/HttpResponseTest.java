package com.goryaninaa.web.HttpServer.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

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
		
		String fact = httpResponse.getResponseString();
		String expected = "HTTP/1.1 404 Not Found\n"
				+ "Server: RagingServer\n"
				+ "\n"
				+ "<p>Hello!</p>";
		
		assertEquals(expected, fact);
	}
	
	@Test
	void httpResponseShouldCorrectlyFormWith3Arg() {
		Map<String, String> headers = new LinkedHashMap<>(4, 0.75f, false);
		
		headers.put("Date", "Sun, 18 Oct 2012 10:36:20 GMT");
		headers.put("Content-Length", "230");
		headers.put("Connection", "Closed");
		headers.put("Content-Type", "text/html; charset=iso-8859-1");
		
		String body = "<p>Hello!</p>";
		
		HttpResponse httpResponse = new HttpResponse(HttpResponseCode.NOTFOUND, headers, body);
		
		String fact = httpResponse.getResponseString();
		String expected = "HTTP/1.1 404 Not Found\n"
				+ "Server: RagingServer\n"
				+ "Date: Sun, 18 Oct 2012 10:36:20 GMT\n"
				+ "Content-Length: 230\n"
				+ "Connection: Closed\n"
				+ "Content-Type: text/html; charset=iso-8859-1\n"
				+ "\n"
				+ "<p>Hello!</p>";
		
		assertEquals(expected, fact);
	}
}
