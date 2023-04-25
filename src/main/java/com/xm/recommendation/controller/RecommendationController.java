package com.xm.recommendation.controller;

import com.xm.recommendation.models.CryptoRecordDto;
import com.xm.recommendation.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currency")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{crypto}")
    public CryptoRecordDto getCurrency(@PathVariable String crypto) {
        return this.recommendationService.getCryptoInfo(crypto);
    }

}
