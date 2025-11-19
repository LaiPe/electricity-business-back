package com.laipe.electricitybusiness.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class PowerCalculatorUtil {

    /**
     * Calculate the consumed power over a time interval.
     *
     * @param power     Power in kilowatts (kW)
     * @param startTime Start time of the interval
     * @param endTime   End time of the interval
     * @return Consumed power in kilowatt-hours (kWh)
     */
    public double calculateConsumedPower(double power, LocalDateTime startTime, LocalDateTime endTime) {
        long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
        double durationInHours = durationInSeconds / 3600.0;
        return power * durationInHours;
    }

    /**
     * Calculate the cost based on consumed power and price per kWh.
     *
     * @param consumedPower Consumed power in kilowatt-hours (kWh)
     * @param pricePerKWh   Price per kilowatt-hour
     * @return Total cost
     */
    public double calculateCost(double consumedPower, double pricePerKWh) {
        return consumedPower * pricePerKWh;
    }
}
