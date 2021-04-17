package com.sweetpeatime.sweetpeatime.scheduleTask;

import com.sweetpeatime.sweetpeatime.entities.PromotionDetail;
import com.sweetpeatime.sweetpeatime.repositories.PromotionDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PromotionSchedule {
    private static final Logger log = LoggerFactory.getLogger(PromotionSchedule.class);

    @Autowired
    PromotionDetailRepository promotionDetailRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Scheduled(cron = "0 0 0,5 ? * *")
    //    @Scheduled(fixedRate = 5000)
    public void setInactivePromotion() {
        Date date = new Date();

        List<PromotionDetail> promotionDetails = this.promotionDetailRepository.findPromotionDetailsByStatus("active");

        for (PromotionDetail promotionDetail : promotionDetails) {
            if (promotionDetail.getExpiryDate().before(date)) {
                promotionDetail.setStatus("inactive");
                this.promotionDetailRepository.saveAndFlush(promotionDetail);
            }
        }
    }
}
