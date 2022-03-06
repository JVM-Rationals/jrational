package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class RationalNumberExtensionTest {

    @Test
    void bigIntegerValue() {
        assertEquals(BigInteger.ZERO, Rational.of(99, 100).bigIntegerValue());

        assertEquals(BigInteger.ONE, Rational.of(100, 100).bigIntegerValue());
        assertEquals(BigInteger.ONE, Rational.of(101, 100).bigIntegerValue());

        assertEquals(BigInteger.valueOf(154), Rational.of(15499, 100).bigIntegerValue());
        assertEquals(BigInteger.valueOf(155), Rational.of(15500, 100).bigIntegerValue());
        assertEquals(BigInteger.valueOf(155), Rational.of(15501, 100).bigIntegerValue());
    }

    @Test
    void intValue() {
        assertEquals(0, Rational.of(99, 100).intValue());

        assertEquals(1, Rational.of(100, 100).intValue());
        assertEquals(1, Rational.of(101, 100).intValue());

        assertEquals(154, Rational.of(15499, 100).intValue());
        assertEquals(155, Rational.of(15500, 100).intValue());
        assertEquals(155, Rational.of(15501, 100).intValue());

        assertEquals(Integer.MIN_VALUE, Rational.of("-2147483648").intValue());
        assertEquals(Integer.MAX_VALUE, Rational.of("2147483647").intValue());
    }

    @Test
    void longValue() {
        assertEquals(0, Rational.of(99, 100).longValue());

        assertEquals(1, Rational.of(100, 100).longValue());
        assertEquals(1, Rational.of(101, 100).longValue());

        assertEquals(154, Rational.of(15499, 100).longValue());
        assertEquals(155, Rational.of(15500, 100).longValue());
        assertEquals(155, Rational.of(15501, 100).longValue());

        assertEquals(Integer.MIN_VALUE, Rational.of("-2147483648").longValue());
        assertEquals(Integer.MAX_VALUE, Rational.of("2147483647").longValue());

        assertEquals(Long.MIN_VALUE, Rational.of("-9223372036854775808").longValue());
        assertEquals(Long.MAX_VALUE, Rational.of("9223372036854775807").longValue());
    }

    @Test
    void bigDecimalValue() {
        // Compare with .compareTo due to scale stuff.
        assertEquals(0, BigDecimal.ZERO.compareTo(Rational.ZERO.bigDecimalValue()));
        assertEquals(0, BigDecimal.ONE.compareTo(Rational.ONE.bigDecimalValue()));

        assertEquals(0, BigDecimal.valueOf(0.5).compareTo(Rational.of(1, 2).bigDecimalValue()));
        assertEquals(0, BigDecimal.valueOf(0.1).compareTo(Rational.of(1, 10).bigDecimalValue()));

        assertEquals(0, BigDecimal.valueOf(0.5).compareTo(Rational.of(0.5).bigDecimalValue()));
        assertEquals(0, BigDecimal.valueOf(0.1).compareTo(Rational.of("0.1").bigDecimalValue()));
    }

    @Test
    void floatValue() {
        // Compare with .compareTo due to scale stuff.
        assertEquals(0.0f, Rational.ZERO.floatValue());
        assertEquals(1.0f, Rational.ONE.floatValue());

        assertEquals(0.5f, Rational.of(1, 2).floatValue());
        assertEquals(0.1f, Rational.of(1, 10).floatValue());

        assertEquals(0.5f, Rational.of(0.5).floatValue());
        assertEquals(0.1f, Rational.of(0.1).floatValue());

        assertEquals(Float.MIN_NORMAL, Rational.of(Float.MIN_NORMAL).floatValue());
        assertEquals(Float.MIN_VALUE, Rational.of(Float.MIN_VALUE).floatValue());
        assertEquals(Float.MAX_VALUE, Rational.of(Float.MAX_VALUE).floatValue());
    }

    @Test
    void doubleValue() {
        // Compare with .compareTo due to scale stuff.
        assertEquals(0.0, Rational.ZERO.doubleValue());
        assertEquals(1.0, Rational.ONE.doubleValue());

        assertEquals(0.5, Rational.of(1, 2).doubleValue());
        assertEquals(0.1, Rational.of(1, 10).doubleValue());

        assertEquals(0.5, Rational.of(0.5).doubleValue());
        assertEquals(0.1, Rational.of(0.1).doubleValue());

        assertEquals(Double.MIN_NORMAL, Rational.of(Double.MIN_NORMAL).doubleValue());
        assertEquals(Double.MIN_VALUE, Rational.of(Double.MIN_VALUE).doubleValue());
        assertEquals(Double.MAX_VALUE, Rational.of(Double.MAX_VALUE).doubleValue());
    }
}
