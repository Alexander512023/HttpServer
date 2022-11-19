package com.goryaninaa.web.HttpServer.json.serializer;

import java.util.List;

public class SensorDTO {
	
	private String name;
	private List<MeasurementDTO> measurements;

	public SensorDTO() {
	}
	
	public SensorDTO(String name, List<MeasurementDTO> measurements) {
		super();
		this.name = name;
		this.measurements = measurements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MeasurementDTO> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(List<MeasurementDTO> measurements) {
		this.measurements = measurements;
	}

}
