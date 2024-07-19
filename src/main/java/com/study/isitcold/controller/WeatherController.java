package com.study.isitcold.controller;

import com.study.isitcold.model.Weather;
import com.study.isitcold.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URISyntaxException;

@Controller
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public String getWeather(@RequestParam("nx") int nx, @RequestParam("ny") int ny, Model model) {
        try {
            Weather weather = weatherService.getWeather(nx, ny);
            model.addAttribute("weather", weather);
            return "weather";
        } catch (URISyntaxException e) {
            model.addAttribute("error", "Invalid URL: " + e.getMessage());
            return "error";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
