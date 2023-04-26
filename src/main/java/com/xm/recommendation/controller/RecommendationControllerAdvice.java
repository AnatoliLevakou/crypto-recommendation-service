package com.xm.recommendation.controller;

import com.xm.recommendation.exceptions.CryptoNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RecommendationControllerAdvice {
    @ExceptionHandler(CryptoNotExistException.class)
    public ProblemDetail handleCryptoNotExistsException(Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
