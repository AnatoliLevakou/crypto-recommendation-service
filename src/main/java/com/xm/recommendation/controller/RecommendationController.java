package com.xm.recommendation.controller;

import com.xm.recommendation.models.CryptoRecordDto;
import com.xm.recommendation.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crypto")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Endpoint return a descending sorted list of all the cryptos,
     * comparing the normalized range.
     *
     * @return List<CryptoRecordDto>
     */
    @GetMapping("/sorted")
    public List<CryptoRecordDto> getCryptos() {
        return this.recommendationService.getCryptosInfo();
    }

    /**
     * Endpoint return the oldest/newest/min/max values for a requested
     * crypto.
     *
     * @param crypto Symbol for searching
     * @return CryptoRecordDto
     */
    @GetMapping("/{crypto}")
    public CryptoRecordDto getCrypto(@PathVariable String crypto) {
        return this.recommendationService.getCryptoInfo(crypto);
    }

    /**
     * Endpoint that will return the crypto with the highest normalized range for a
     * specific day.
     *
     * @param date Specified date for searching
     * @return CryptoRecordDto
     */
    @GetMapping("/best")
    public CryptoRecordDto getBestCrypto(@RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return this.recommendationService.getBestCryptoForDate(date);
    }
}
