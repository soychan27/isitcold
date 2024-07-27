package com.study.isitcold.controller;

import com.study.isitcold.model.Location;
import com.study.isitcold.model.Weather;
import com.study.isitcold.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/locationChoice")
    public String getLocationChoice(Model model) {
        List<String> level1Locations = locationService.getLevel1Locations();
        model.addAttribute("level1Locations", level1Locations);
        return "locationChoice";
    }

    @GetMapping("/getSubLocations")
    @ResponseBody
    public List<String> getSubLocations(@RequestParam("level") int level, @RequestParam("region") String region) {
        return locationService.getSubLocations(level, region);
    }

    @GetMapping("/getCoordinates")
    @ResponseBody
    public Map<String, String> getCoordinates(@RequestParam("level1") String level1, @RequestParam("level2") String level2, @RequestParam("level3") String level3) {
        Location loc = locationService.getLocationByLevels(level1, level2, level3);
        Map<String, String> result = new HashMap<>();
        if (loc != null) {
            result.put("nx", String.valueOf(loc.getNx()));
            result.put("ny", String.valueOf(loc.getNy()));
            result.put("region", loc.getRegion());
        } else {
            result.put("error", "Location not found");
        }
        return result;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam("nx") String nx, @RequestParam("ny") String ny, @RequestParam("region") String region, Model model) {
        model.addAttribute("nx", nx);
        model.addAttribute("ny", ny);
        model.addAttribute("region", region);

        // 여기에 weather 객체를 생성하고 formattedDate 속성을 설정하는 부분을 추가합니다.
        Weather weather = new Weather();
        weather.setDate("20240721"); // 예시 날짜 설정
        weather.setTime("1230"); // 예시 시간 설정
        model.addAttribute("weather", weather);

        return "weather";
    }
}
