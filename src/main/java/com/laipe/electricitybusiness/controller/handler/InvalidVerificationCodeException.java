package com.laipe.electricitybusiness.controller.handler;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Invalid verification code");
    }
}
