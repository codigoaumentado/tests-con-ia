package com.codigoaumentado.descuentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record Coupon(String code, Type type, BigDecimal value, BigDecimal minimumAmount, LocalDate expiresOn) {

    public enum Type { PERCENTAGE, FIXED }

    public Coupon {
        Objects.requireNonNull(code, "code is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(value, "value is required");
        Objects.requireNonNull(minimumAmount, "minimumAmount is required");
        Objects.requireNonNull(expiresOn, "expiresOn is required");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("value must be positive");
        }
    }

    public boolean isValidOn(LocalDate day) {
        return !day.isAfter(expiresOn);
    }

    public boolean appliesTo(BigDecimal subtotal) {
        return subtotal.compareTo(minimumAmount) >= 0;
    }
}
