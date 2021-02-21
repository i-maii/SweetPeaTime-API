package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.Florist;
import com.sweetpeatime.sweetpeatime.repositories.FloristRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/florist")
public class FloristController {

    @Autowired
    FloristRepository floristRepository;

    @GetMapping(value="/getAll")
    public List<Florist> getAllFlorist() {
        return this.floristRepository.findAll();
    }
}
