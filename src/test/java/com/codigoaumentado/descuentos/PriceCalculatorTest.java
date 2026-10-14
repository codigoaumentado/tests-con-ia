package com.codigoaumentado.descuentos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests "útiles": cada test verifica un valor exacto y los límites de cada regla.
 */
class PriceCalculatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 21);
    private final PriceCalculator calculator = new PriceCalculator();

    private static Coupon percentage(String value, String minimum, LocalDate expiresOn) {
        return new Coupon("PROMO", Coupon.Type.PERCENTAGE, new BigDecimal(value), new BigDecimal(minimum), expiresOn);
    }

    private static Coupon fixed(String value, String minimum, LocalDate expiresOn) {
        return new Coupon("PROMO", Coupon.Type.FIXED, new BigDecimal(value), new BigDecimal(minimum), expiresOn);
    }

    private BigDecimal price(String subtotal, Coupon coupon) {
        return calculator.finalPrice(new BigDecimal(subtotal), coupon, TODAY);
    }

    @ParameterizedTest(name = "{0} con {1} % → {2}")
    @CsvSource({
            "100.00, 10, 90.00",
            "100.00, 50, 50.00",
            "100.00, 60, 50.00",   // el porcentaje tiene tope de 50
            "19.99,  15, 16.99",   // redondeo HALF_UP a 2 decimales
    })
    void appliesPercentageWithCapAndRounding(String subtotal, String percent, String expected) {
        assertThat(price(subtotal, percentage(percent, "0", TODAY))).isEqualByComparingTo(expected);
    }

    @ParameterizedTest(name = "{0} − {1} → {2}")
    @CsvSource({
            "100.00, 15,  85.00",
            "100.00, 100, 0.00",
            "100.00, 150, 0.00",   // nunca negativo
    })
    void appliesFixedAmountWithoutGoingBelowZero(String subtotal, String amount, String expected) {
        assertThat(price(subtotal, fixed(amount, "0", TODAY))).isEqualByComparingTo(expected);
    }

    @Test
    void appliesCouponOnItsLastValidDay() {
        assertThat(price("100.00", fixed("15", "0", TODAY))).isEqualByComparingTo("85.00");
    }

    @Test
    void ignoresCouponTheDayAfterItExpires() {
        assertThat(price("100.00", fixed("15", "0", TODAY.minusDays(1)))).isEqualByComparingTo("100.00");
    }

    @Test
    void appliesCouponWhenSubtotalEqualsMinimum() {
        assertThat(price("50.00", fixed("10", "50.00", TODAY))).isEqualByComparingTo("40.00");
    }

    @Test
    void ignoresCouponOneCentBelowMinimum() {
        assertThat(price("49.99", fixed("10", "50.00", TODAY))).isEqualByComparingTo("49.99");
    }

    @Test
    void returnsSubtotalRoundedWhenThereIsNoCoupon() {
        assertThat(price("10.005", null)).isEqualTo(new BigDecimal("10.01"));
    }

    @Test
    void acceptsZeroSubtotal() {
        assertThat(price("0", fixed("10", "0", TODAY))).isEqualByComparingTo("0.00");
    }

    @Test
    void rejectsNegativeSubtotal() {
        assertThatThrownBy(() -> price("-0.01", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("subtotal must not be negative");
    }

    @Test
    void rejectsCouponWithNonPositiveValue() {
        assertThatThrownBy(() -> fixed("0", "0", TODAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be positive");
    }
}
