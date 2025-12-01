package com.ticketing.strategy;

public interface PricingStrategy {
    double calculatePrice(double basePrice);
    String getStrategyName();
    double getDiscountPercentage();
}