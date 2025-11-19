package com.laipe.electricitybusiness.controller.handler;

public class InvalidBookingState extends RuntimeException {
    public InvalidBookingState(String message) {
        super(message);
    }
}
