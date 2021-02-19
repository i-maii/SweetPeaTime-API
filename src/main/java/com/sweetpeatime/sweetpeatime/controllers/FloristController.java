package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import com.sweetpeatime.sweetpeatime.repositories.impl.FloristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/florist")
public class FloristController {

    @Autowired
    FloristRepository floristRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping(value="/getAll")
    public List<Florist> getAllFlorist() {
        return this.floristRepository.findAll();
    }
}
