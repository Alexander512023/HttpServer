package com.goryaninaa.web.HttpServer.json.serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonSerializerTest {

	private SensorDTO sensorDTO;

	@BeforeEach
	void init() {
		MeasurementDTO measurementDTO1 = new MeasurementDTO(15.5, true);
		MeasurementDTO measurementDTO2 = new MeasurementDTO(30.5, false);
		
		List<MeasurementDTO> measurements = new ArrayList<>();
		measurements.add(measurementDTO1);
		measurements.add(measurementDTO2);
		
		this.sensorDTO = new SensorDTO("Sensor", measurements);
	}

	@Test
	void testSerialize() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		JsonSerializer serializer = new JsonSerializer();

		System.out.println(serializer.serialize(sensorDTO));
	}

}
