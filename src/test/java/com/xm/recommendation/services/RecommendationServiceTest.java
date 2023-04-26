package com.xm.recommendation.services;

import com.xm.recommendation.exceptions.CryptoNotExistException;
import com.xm.recommendation.models.CryptoRecordDao;
import com.xm.recommendation.models.CryptoRecordDto;
import com.xm.recommendation.repositories.CryptoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private CryptoRepository cryptoRepository;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(cryptoRepository);
    }

    @Test
    void whenValidCryptoInfoRequested_expectedResultReturned() {
        // given
        final String symbol = "BTC";
        CryptoRecordDao dao = this.prepareDaoResponse(symbol);

        when(cryptoRepository.existsBySymbol(symbol)).thenReturn(true);
        when(cryptoRepository.findDataForSymbol(symbol)).thenReturn(dao);

        // when
        CryptoRecordDto dto = recommendationService.getCryptoInfo(symbol);

        // then
        this.validateReturnedDtoAgainstDao(dao, dto);

        verify(cryptoRepository, times(1)).existsBySymbol(symbol);
        verify(cryptoRepository, times(1)).findDataForSymbol(symbol);

        verifyNoMoreInteractions(cryptoRepository);
    }

    @Test
    void whenNotValidCryptoInfoRequested_expectedExceptionThrown() {
        // given
        final String symbol = "XYZ";

        // when
        assertThrows(CryptoNotExistException.class, () -> recommendationService.getCryptoInfo(symbol));

        // then
        verify(cryptoRepository, times(1)).existsBySymbol(symbol);
        verifyNoMoreInteractions(cryptoRepository);
    }

    @Test
    void whenBestCryptoInfoRequested_expectedResultReturned() {
        // given
        final String symbol = "ETH";
        LocalDate date = LocalDate.now();

        CryptoRecordDao dao = this.prepareDaoResponse(symbol);
        when(cryptoRepository.findBestCryptoForDate(any())).thenReturn(dao);

        // when
        CryptoRecordDto dto = recommendationService.getBestCryptoForDate(date);

        // then
        this.validateReturnedDtoAgainstDao(dao, dto);

        verify(cryptoRepository, times(1)).findBestCryptoForDate(any());
        verifyNoMoreInteractions(cryptoRepository);
    }

    @Test
    void whenSortedCryptosRequested_expectedResultReturned() {
        // given
        final String symbol = "ETH";

        CryptoRecordDao ethDao = this.prepareDaoResponse("ETH");
        CryptoRecordDao xrpDao = this.prepareDaoResponse("XRP");

        when(cryptoRepository.findNormalizedData()).thenReturn(List.of(ethDao, xrpDao));

        // when
        List<CryptoRecordDto> dto = recommendationService.getCryptosInfo();

        // then
        this.validateReturnedDtoAgainstDao(ethDao, dto.get(0));
        this.validateReturnedDtoAgainstDao(xrpDao, dto.get(1));

        verify(cryptoRepository, times(1)).findNormalizedData();
        verifyNoMoreInteractions(cryptoRepository);
    }

    private void validateReturnedDtoAgainstDao(CryptoRecordDao dao, CryptoRecordDto dto) {
        assertEquals(dao.getSymbol(), dto.getSymbol());
        assertEquals(dao.getOldestPrice(), dto.getOldestPrice());
        assertEquals(dao.getNewestPrice(), dto.getNewestPrice());
        assertEquals(dao.getMinPrice(), dto.getMinPrice());
        assertEquals(dao.getMaxPrice(), dto.getMaxPrice());
        assertEquals(dao.getNormRange(), dto.getNormRange());
    }

    private CryptoRecordDao prepareDaoResponse(String symbol) {
        return new CryptoRecordDao() {
            @Override
            public String getSymbol() {
                return symbol;
            }

            @Override
            public BigDecimal getOldestPrice() {
                return new BigDecimal(10);
            }

            @Override
            public BigDecimal getNewestPrice() {
                return new BigDecimal(100);
            }

            @Override
            public BigDecimal getMinPrice() {
                return new BigDecimal(1);
            }

            @Override
            public BigDecimal getMaxPrice() {
                return new BigDecimal(150);
            }

            @Override
            public BigDecimal getNormRange() {
                return new BigDecimal("0.445");
            }
        };
    }
}
