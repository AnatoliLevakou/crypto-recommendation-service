package com.xm.recommendation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetCryptosInvoked_expectedListOfSortedCryptosReturned() throws Exception {
        // given
        final String path = "/api/v1/crypto/sorted";
        final MockHttpServletRequestBuilder requestBuilder = get(path);

        // when
        final ResultActions result = mockMvc.perform(requestBuilder);

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].symbol").value("ETH"))
                .andExpect(jsonPath("$[1].symbol").value("XRP"))
                .andExpect(jsonPath("$[2].symbol").value("DOGE"))
                .andExpect(jsonPath("$[3].symbol").value("LTC"))
                .andExpect(jsonPath("$[4].symbol").value("BTC"));
    }

    @Test
    void whenBestCryptoInvoked_expectedBestCryptoReturnedForDay() throws Exception {
        // given
        final String path = "/api/v1/crypto/best?date=2022-01-02";
        final MockHttpServletRequestBuilder requestBuilder = get(path);

        // when
        final ResultActions result = mockMvc.perform(requestBuilder);

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.symbol").value("ETH"))
                .andExpect(jsonPath("$.oldestPrice").value(3747.0000))
                .andExpect(jsonPath("$.newestPrice").value(3795.0600))
                .andExpect(jsonPath("$.minPrice").value(3743.1700))
                .andExpect(jsonPath("$.maxPrice").value(3823.8200))
                .andExpect(jsonPath("$.normRange").value(0.02154590894883213960359801986017));
    }

    @Test
    void whenGetCryptoInvoked_expectedRequestCryptoInfoReturned() throws Exception {
        // given
        final String path = "/api/v1/crypto/XRP";
        final MockHttpServletRequestBuilder requestBuilder = get(path);

        // when
        final ResultActions result = mockMvc.perform(requestBuilder);

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.symbol").value("XRP"))
                .andExpect(jsonPath("$.oldestPrice").value(0.8298))
                .andExpect(jsonPath("$.newestPrice").value(0.5867))
                .andExpect(jsonPath("$.minPrice").value(0.5616))
                .andExpect(jsonPath("$.maxPrice").value(0.8458))
                .andExpect(jsonPath("$.normRange").value(0.50605413105413105413105413105413));
    }

    @Test
    void whenGetCryptoInvokedWithIncorrectCrypto_expectedErrorMessageReturned() throws Exception {
        // given
        final String path = "/api/v1/crypto/XYZ";
        final MockHttpServletRequestBuilder requestBuilder = get(path);

        // when
        final ResultActions result = mockMvc.perform(requestBuilder);

        // then
        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Requested crypto XYZ not exist in system."));
    }

}
