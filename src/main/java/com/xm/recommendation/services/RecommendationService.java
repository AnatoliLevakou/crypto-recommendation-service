package com.xm.recommendation.services;

import com.xm.recommendation.exceptions.CryptoNotExistException;
import com.xm.recommendation.models.CryptoRecordDao;
import com.xm.recommendation.models.CryptoRecordDto;
import com.xm.recommendation.repositories.CryptoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private CryptoRepository cryptoRepository;

    public List<CryptoRecordDto> getCryptosInfo() {
        final List<CryptoRecordDao> daos = this.cryptoRepository.findNormalizedData();

        return daos.stream().map(this::convert).collect(Collectors.toList());
    }

    public CryptoRecordDto getCryptoInfo(String symbol) {
        this.validate(symbol);
        final CryptoRecordDao dao = this.cryptoRepository.findDataForSymbol(symbol);

        return this.convert(dao);
    }

    public CryptoRecordDto getBestCryptoForDate(LocalDate date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        final String formattedString = date.format(formatter);
        final CryptoRecordDao dao = this.cryptoRepository.findBestCryptoForDate(formattedString);

        return this.convert(dao);
    }

    private void validate(final String symbol) {
        if (!this.cryptoRepository.existsBySymbol(symbol)) {
            throw new CryptoNotExistException(symbol);
        }
    }

    private CryptoRecordDto convert(final CryptoRecordDao dao) {
        final CryptoRecordDto dto = new CryptoRecordDto();
        dto.setSymbol(dao.getSymbol());
        dto.setMinPrice(dao.getMinPrice());
        dto.setMaxPrice(dao.getMaxPrice());
        dto.setOldestPrice(dao.getOldestPrice());
        dto.setNewestPrice(dao.getNewestPrice());
        dto.setNormRange(dao.getNormRange());

        return dto;
    }
}
