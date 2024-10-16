package com.example.WalletApplication.enums;

public enum CurrencyType {
    USD(1), INR(85);
    private final double conversionFactor;
    CurrencyType(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
    public double toBase(double value) {
        return value / this.conversionFactor;
    }
    public double fromBase(double value) {
        return value * this.conversionFactor;
    }

}
