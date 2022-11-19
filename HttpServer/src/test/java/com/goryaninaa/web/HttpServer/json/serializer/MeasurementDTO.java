package com.goryaninaa.web.HttpServer.json.serializer;

public class MeasurementDTO {

	private double value;
	
	private Boolean raining;
	
	public MeasurementDTO() {
		super();
	}

	public MeasurementDTO(double value, boolean raining) {
		super();
		this.value = value;
		this.raining = raining;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isRaining() {
		return raining;
	}

	public void setRaining(boolean raining) {
		this.raining = raining;
	}
}
