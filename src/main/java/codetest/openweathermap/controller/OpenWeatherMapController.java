package codetest.openweathermap.controller;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import codetest.openweathermap.exception.InvalidLocationException;
import codetest.openweathermap.model.OpenWeatherMapData;
import codetest.openweathermap.service.OpenWeatherMapService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

@RestController
@RequestMapping("/")
public class OpenWeatherMapController {
    private OpenWeatherMapService service;

    @Autowired
    Supplier<BucketConfiguration> bucketConfig;

    @Autowired
    ProxyManager<String> proxyManager;
    
    @Autowired
    public OpenWeatherMapController(OpenWeatherMapService service) {
        this.service = service;
    }

    @GetMapping("/data/2.5/weather")
    public ResponseEntity<Object> obtainAndSaveWeatherData(@RequestParam String countryCode, @RequestParam String city, 
            @RequestParam String state, @RequestParam String appid) {
        
        // check for API rate limiting 
        Bucket bucket = proxyManager
                .builder()
                .build(appid, bucketConfig);
        System.out.println(bucket.getAvailableTokens());
//        Bucket bucket = proxyManager.getProxy(apiKey, bucketConfiguration); // bucket4j ver. 8.14 
        if(!bucket.tryConsume(1))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                            "error", "Too many requests"));
        
        try {
            // 1. call Open Weather Map API and obtain weather data 
            OpenWeatherMapData weatherData = service.obtainWeatherDataFromOpenWeatherAPI(countryCode, city, state,
                    appid); 
            
            // 2. save weather data to DB
            service.saveWeatherData(weatherData);
            
            return ResponseEntity.status(HttpStatus.CREATED)
        //        .build()
                    .body(city + ", " + state + ", " + countryCode.toUpperCase() + ": " + weatherData.getWeatherDesc());
        } catch (HttpClientErrorException e) {
            // HTTP 401 - invalid API key
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Invalid API key",
                            "message", e.getMessage()));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal server error",
                            "message", e.getMessage(), //TODO "Error in processing geocoding response JSON: " + geocodingResponse,
                            "Exception stack trace", e));
        } catch (InvalidLocationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Not found",
                            "message", "Invalid input location - " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal server error",
                            "message", e.getMessage(),
                            "Exception stack trace", e));

        }
    }
}
