package com.rishabh.meeting.service;

import com.rishabh.meeting.entity.City;
import com.rishabh.meeting.repository.CityRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CityService {
    private final CityRepository cityRepository;
    public CityService(CityRepository cityRepository){
        this.cityRepository = cityRepository;
    }
    public List<City> getAllCities(){
        return cityRepository.findAll();
    }
}
