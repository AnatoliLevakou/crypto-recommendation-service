package com.xm.recommendation.exceptions;

public class CryptoNotExistException extends RuntimeException {

    public CryptoNotExistException(String message) {
        super(String.format("Requested crypto %s not exist in system.", message));
    }
}
