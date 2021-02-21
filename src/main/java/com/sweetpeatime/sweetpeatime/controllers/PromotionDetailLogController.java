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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    PromotionDetailLogRepository promotionDetailLogRepository;

    @GetMapping(value = "")
    public List<PromotionDetailLog> getPromotion(
            @RequestParam(value = "isNormal", required = true) boolean isNormal
    ) throws ParseException {
        String dateInStr = this.dateFormat.format(new Date());
        Date date = this.dateFormat.parse(dateInStr);

        if (isNormal)
            return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "normal", "active");
        else
            return this.promotionDetailLogRepository.findPromotionDetailLogsByPromotionDateAndPromotionTypeAndStatusOrderBySequenceAsc(date, "current", "active");
    }
}
