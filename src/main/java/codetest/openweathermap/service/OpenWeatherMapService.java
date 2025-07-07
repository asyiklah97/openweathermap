package codetest.openweathermap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import codetest.openweathermap.exception.InvalidLocationException;
import codetest.openweathermap.model.OpenWeatherMapData;
import codetest.openweathermap.repository.OpenWeatherMapRepository;

@Service
public class OpenWeatherMapService {
    private static final String CURRENT_WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid={API key}";
    private static final String GEOCODING_API_URL_US = "http://api.openweathermap.org/geo/1.0/direct?q={city name},{state code},{country code}&limit={limit}&appid={API key}";
    private static final String GEOCODING_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q={city name},{country code}&limit={limit}&appid={API key}";

    private OpenWeatherMapRepository repository;

    @Autowired
    public OpenWeatherMapService(OpenWeatherMapRepository repository) {
        this.repository = repository;
    }
        
    public OpenWeatherMapData obtainWeatherDataFromOpenWeatherAPI(String countryCode, String city, String state,
            String appid) throws InvalidLocationException, JsonProcessingException {
        
        // 1. call geocoding API 
        RestClient restClient = RestClient.create();
        String geocodingResponse;
        if(state.isBlank())
            geocodingResponse = restClient.get()
                    .uri(GEOCODING_API_URL, city, countryCode, 1, appid)
                    .retrieve()
                    .body(String.class); // may throw HttpClientErrorException HTTP 401
        else
            geocodingResponse = restClient.get()
                    .uri(GEOCODING_API_URL_US, city, state, countryCode, 1, appid)
                    .retrieve()
                    .body(String.class); // may throw HttpClientErrorException HTTP 401
        System.out.println(geocodingResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        
        JsonNode geocodingNode = objectMapper.readTree(geocodingResponse);
        
        if(geocodingNode.get(0) == null) // geocoding is not found
            throw new InvalidLocationException("Invalid location based on input city " + city + ", state " + state
                    + "country code " + countryCode);
        
        double lattitude = geocodingNode.get(0)
                .get("lat")
                .asDouble();
        double longitude = geocodingNode.get(0)
                .get("lon")
                .asDouble();
        
        // 2. call current weather API, get weather data
        String weatherData = restClient.get()
                .uri(CURRENT_WEATHER_API_URL, lattitude, longitude, appid)
                .retrieve()
                .body(String.class);
        System.out.println(weatherData);
        
        JsonNode weatherDataNode = objectMapper.readTree(weatherData);
        
        String weatherDesc = weatherDataNode.get("weather")
                .get(0)
                .get("description")
                .textValue();
        System.out.println(weatherDesc);
        
        long unixTime = weatherDataNode.get("dt")
                .asLong();
        
        OpenWeatherMapData weatherDataDTO = new OpenWeatherMapData(unixTime, city, state, countryCode, lattitude,
                longitude, weatherDesc);
        return weatherDataDTO;
    }
    
    public OpenWeatherMapData saveWeatherData(OpenWeatherMapData dto) {
        return repository.save(dto);
    }
}
