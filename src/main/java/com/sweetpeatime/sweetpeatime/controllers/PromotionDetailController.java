package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/promotionDetail")
public class PromotionDetailController {

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    @GetMapping(value = "/currentPromotion")
    public List<PromotionDetail> getCurrentPromotion() {
        return this.promotionDetailRepository.findPromotionDetailsByStatus("active");
    }
}
