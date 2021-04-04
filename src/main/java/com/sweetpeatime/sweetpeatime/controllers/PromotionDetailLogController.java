package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetailLog;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value="/promotionDetailLog")
public class PromotionDetailLogController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @GetMapping(value = "/normal")
    public List<PromotionDetailLog> getNormalPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);

        return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "normal", "active");
    }

    @GetMapping(value = "/current")
    public List<PromotionDetailLog> getCurrentPromotion() throws ParseException {
        String dateInStr = dateFormat.format(new Date());
        Date date = dateFormat.parse(dateInStr);

        return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "current", "active");
    }

    @GetMapping(value = "/suggest")
    public List<PromotionDetailLog> getSuggestPromotion() {
        return this.promotionDetailLogRepository.findPromotionDetailLogsByStatusOrderBySequenceAsc("active");
    }
}
