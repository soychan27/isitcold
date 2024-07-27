package com.study.isitcold.service;

import com.study.isitcold.model.Location;
import com.study.isitcold.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<String> getLevel1Locations() {
        return locationRepository.findAll().stream()
                .map(Location::getLevel1)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getSubLocations(int level, String region) {
        if (level == 1) {
            return locationRepository.findAll().stream()
                    .filter(location -> region.equals(location.getLevel1()))
                    .map(Location::getLevel2)
                    .distinct()
                    .collect(Collectors.toList());
        } else if (level == 2) {
            return locationRepository.findAll().stream()
                    .filter(location -> region.equals(location.getLevel2()))
                    .map(Location::getLevel3)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    public Location getLocationByLevels(String level1, String level2, String level3) {
        return locationRepository.findByLevel1AndLevel2AndLevel3(level1, level2, level3);
    }
}
