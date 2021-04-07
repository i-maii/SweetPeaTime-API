package com.sweetpeatime.sweetpeatime.controllers;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetailLog;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @GetMapping(value = "/promotionDetailLog")
    public List<PromotionDetailLog> getPromotionDetailLog() throws ParseException {

        return this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionTypeOrderByLotStock("active", 0, "normal");

    }

    @GetMapping(value = "/promotionDetailLogRemain")
    public List<PromotionDetailLog> getPromotionDetailLogRemain() throws ParseException {

        return this.promotionDetailLogRepository.findPromotionDetailLogsByStatusAndQuantityGreaterThanAndPromotionType("active", 0, "remain");
    }
}
