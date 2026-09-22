package com.rishabh.meeting.controller;

import com.rishabh.meeting.entity.City;
import com.rishabh.meeting.repository.CityRepository;
import com.rishabh.meeting.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private final CityService cityService ;

    public CityController(CityService cityService){
        this.cityService = cityService;
    }
    @GetMapping
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllCities());
    }
}
