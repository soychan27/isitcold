package com.study.isitcold.controller;

import com.study.isitcold.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class DataLoadController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/load-data")
    public String loadData() {
        try {
            excelService.loadData();
            return "redirect:/data-loaded-success";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/data-loaded-fail";
        }
    }
}
