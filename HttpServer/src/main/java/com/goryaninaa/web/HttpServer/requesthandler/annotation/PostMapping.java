package com.goryaninaa.web.HttpServer.requesthandler.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {

	String value();

}
