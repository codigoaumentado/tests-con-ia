package com.codigoaumentado.descuentos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class PriceCalculator {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal MAX_PERCENTAGE = new BigDecimal("50");

    public BigDecimal finalPrice(BigDecimal subtotal, Coupon coupon, LocalDate today) {
        Objects.requireNonNull(subtotal, "subtotal is required");
        Objects.requireNonNull(today, "today is required");
        if (subtotal.signum() < 0) {
            throw new IllegalArgumentException("subtotal must not be negative");
        }
        if (coupon == null || !coupon.isValidOn(today) || !coupon.appliesTo(subtotal)) {
            return money(subtotal);
        }
        BigDecimal discount = switch (coupon.type()) {
            case PERCENTAGE -> subtotal.multiply(coupon.value().min(MAX_PERCENTAGE)).divide(HUNDRED);
            case FIXED -> coupon.value();
        };
        return money(subtotal.subtract(discount).max(BigDecimal.ZERO));
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
