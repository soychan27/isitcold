package com.study.isitcold.repository;

import com.study.isitcold.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByLevel1(String level1);

    List<Location> findByLevel2(String level2);

    Location findByLevel1AndLevel2AndLevel3(String level1, String level2, String level3);
}
