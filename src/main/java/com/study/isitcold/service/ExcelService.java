package com.study.isitcold.service;

import com.study.isitcold.model.Location;
import com.study.isitcold.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExcelService {

    @Value("${excel.file.path}")
    private String excelFilePath;

    @Autowired
    private LocationRepository locationRepository;

    private Map<String, Location> locationMap;

    @PostConstruct
    public void init() throws IOException {
        locationMap = new HashMap<>();
        loadData();
    }

    public void loadData() throws IOException {
        // Adjust the minimum inflate ratio to a lower value to avoid Zip bomb detection
        ZipSecureFile.setMinInflateRatio(0.001);

        FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }

            try {
                String level1 = row.getCell(2).getStringCellValue();
                String level2 = row.getCell(3).getStringCellValue();
                String level3 = row.getCell(4).getStringCellValue();

                int nx = parseCellAsInteger(row.getCell(5));
                int ny = parseCellAsInteger(row.getCell(6));

                Location location = new Location();
                location.setLevel1(level1);
                location.setLevel2(level2);
                location.setLevel3(level3);
                location.setRegion(level1 + " " + level2 + " " + level3);
                location.setNx(nx);
                location.setNy(ny);

                // Save location to locationMap
                locationMap.put(level1 + " " + level2 + " " + level3, location);

                // Save location to database
                locationRepository.save(location);
            } catch (Exception e) {
                System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
            }
        }

        workbook.close();
        fileInputStream.close();
    }

    private int parseCellAsInteger(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("Cell cannot be null");
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot parse cell value to integer: " + cell.getStringCellValue());
            }
        } else {
            throw new IllegalArgumentException("Cell type is not supported for conversion to integer");
        }
    }

    public Location getLocation(String region) {
        return locationMap.get(region);
    }
}
