package codetest.openweathermap.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class OpenWeatherMapData {
    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    private String city;

//    @Size(min = 2, max = 2)
    private String state;

    @NotBlank
    @Size(min = 2, max = 2)
    private String countryCode;

    @NotNull
    private double lat;

    @NotNull
    private double lon;

    @NotBlank
    private String weatherDesc;

    public OpenWeatherMapData(String city, String state, String countryCode, double lat, double lon,
            String weatherDesc) {
        super();
        this.city = city;
        this.state = state;
        this.countryCode = countryCode;
        this.lat = lat;
        this.lon = lon;
        this.weatherDesc = weatherDesc;
    }

    @Override
    public String toString() {
        return "OpenWeatherMapData [id=" + id + ", city=" + city + ", state=" + state + ", countryCode=" + countryCode
                + ", lat=" + lat + ", lon=" + lon + ", weatherDesc=" + weatherDesc + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, countryCode, id, lat, lon, state, weatherDesc);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OpenWeatherMapData other = (OpenWeatherMapData) obj;
        return Objects.equals(city, other.city) && Objects.equals(countryCode, other.countryCode) && id == other.id
                && Double.doubleToLongBits(lat) == Double.doubleToLongBits(other.lat)
                && Double.doubleToLongBits(lon) == Double.doubleToLongBits(other.lon)
                && Objects.equals(state, other.state) && Objects.equals(weatherDesc, other.weatherDesc);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public long getId() {
        return id;
    }
}
