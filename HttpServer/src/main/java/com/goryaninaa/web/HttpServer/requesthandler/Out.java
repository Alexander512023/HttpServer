package com.goryaninaa.web.HttpServer.requesthandler;

public interface Out {

	Response httpResponseFrom(HttpResponseCode httpResponseCode);
	
	Response httpResponseFrom(HttpResponseCode httpResponseCode, String body);
    
	<T> Response httpResponseFrom(HttpResponseCode httpResponseCode, T responseObject);

}
