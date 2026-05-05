package com.ipd.Exception;

public class PatientAlreadyTransferredException extends RuntimeException {
    public PatientAlreadyTransferredException(String message) {
        super(message);
    }
}
