package codetest.openweathermap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import codetest.openweathermap.model.OpenWeatherMapData;

public interface OpenWeatherMapRepository extends JpaRepository<OpenWeatherMapData, Long> {

}
