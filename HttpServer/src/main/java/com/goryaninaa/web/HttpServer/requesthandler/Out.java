package com.goryaninaa.web.HttpServer.requesthandler;

import java.util.Map;

public interface Out {

	Response httpResponseFrom(HttpResponseCode httpResponseCode);
	
	Response httpResponseFrom(HttpResponseCode httpResponseCode, String body);
    
	Response httpResponseFrom(HttpResponseCode httpResponseCode, Map<String, String> additionalHeaders, String body);

}
