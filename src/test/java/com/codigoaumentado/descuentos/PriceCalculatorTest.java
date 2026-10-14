package com.codigoaumentado.descuentos;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests "inútiles": ejecutan todo el código (100 % de cobertura) pero casi no verifican nada.
 * Así suelen quedar cuando se le pide a una IA "sube la cobertura" sin más contexto.
 */
class PriceCalculatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 21);
    private final PriceCalculator calculator = new PriceCalculator();

    private static Coupon coupon(Coupon.Type type, String value, String minimum, LocalDate expiresOn) {
        return new Coupon("PROMO", type, new BigDecimal(value), new BigDecimal(minimum), expiresOn);
    }

    @Test
    void calculatesPriceWithPercentageCoupon() {
        BigDecimal price = calculator.finalPrice(new BigDecimal("100"), coupon(Coupon.Type.PERCENTAGE, "10", "0", TODAY), TODAY);
        assertNotNull(price);
    }

    @Test
    void calculatesPriceWithFixedCoupon() {
        BigDecimal price = calculator.finalPrice(new BigDecimal("100"), coupon(Coupon.Type.FIXED, "15", "0", TODAY), TODAY);
        assertNotNull(price);
    }

    @Test
    void calculatesPriceWithoutCoupon() {
        assertNotNull(calculator.finalPrice(new BigDecimal("100"), null, TODAY));
    }

    @Test
    void calculatesPriceWithExpiredCoupon() {
        assertNotNull(calculator.finalPrice(new BigDecimal("100"), coupon(Coupon.Type.FIXED, "15", "0", TODAY.minusDays(1)), TODAY));
    }

    @Test
    void calculatesPriceBelowMinimum() {
        assertNotNull(calculator.finalPrice(new BigDecimal("100"), coupon(Coupon.Type.FIXED, "15", "500", TODAY), TODAY));
    }

    @Test
    void rejectsNegativeSubtotal() {
        assertThrows(IllegalArgumentException.class, () -> calculator.finalPrice(new BigDecimal("-1"), null, TODAY));
    }

    @Test
    void rejectsNonPositiveCouponValue() {
        assertThrows(IllegalArgumentException.class, () -> coupon(Coupon.Type.FIXED, "0", "0", TODAY));
    }
}
