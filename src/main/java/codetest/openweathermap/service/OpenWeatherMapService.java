package codetest.openweathermap.service;

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

    public OpenWeatherMapService(OpenWeatherMapRepository repository) {
        this.repository = repository;
    }
    
    public OpenWeatherMapData saveWeatherData(String countryCode, String city, String state, String appid)
            throws InvalidLocationException, JsonProcessingException {
        
        // 1. call geocoding API 
        RestClient restClient = RestClient.create();
//        String apiKey = "4557160042b4140d3f203916a2860718";
        String geocodingResponse;
        if(state.isBlank())
            geocodingResponse = restClient.get()
                    .uri(GEOCODING_API_URL, city, countryCode, 1, appid)
                    .retrieve()
                    .body(String.class); // may generate HttpClientErrorException HTTP 401
        else
            geocodingResponse = restClient.get()
                    .uri(GEOCODING_API_URL_US, city, state, countryCode, 1, appid)
                    .retrieve()
                    .body(String.class); // may generate HttpClientErrorException HTTP 401
        System.out.println(geocodingResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        
        JsonNode geocodingNode = objectMapper.readTree(geocodingResponse);
        
        if(geocodingNode.get(0) == null)
            throw new InvalidLocationException();
        
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
        
        // 3. save weather data to DB
        OpenWeatherMapData weatherDataDTO = new OpenWeatherMapData(city, state, countryCode, lattitude, longitude,
                weatherDesc);
        
        // 4. return weather description to client 

        return repository.save(weatherDataDTO);
    }
}
