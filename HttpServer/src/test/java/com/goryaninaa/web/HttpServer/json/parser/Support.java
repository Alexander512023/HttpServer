package com.goryaninaa.web.HttpServer.json.parser;

import java.util.Objects;

public class Support {
	private String url;
	private String text;
	
	public Support() {
	}
	
	public Support(String url, String text) {
		super();
		this.url = url;
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Support other = (Support) obj;
		return Objects.equals(text, other.text) && Objects.equals(url, other.url);
	}
	
}
