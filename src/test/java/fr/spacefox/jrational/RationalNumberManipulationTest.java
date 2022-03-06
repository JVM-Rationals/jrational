package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RationalNumberManipulationTest {
    @Test
    void negate() {
        assertSame(Rational.ZERO, Rational.ZERO.negate());
        assertSame(Rational.APPROX_ZERO, Rational.APPROX_ZERO.negate());

        assertEquals(Rational.of(-1), Rational.ONE.negate());
        assertEquals(Rational.ONE, Rational.of(-1).negate());

        assertEquals(Rational.of(3, 5), Rational.of(-3, 5).negate());
        assertEquals(Rational.of(3, 5), Rational.of(3, -5).negate());
        assertEquals(Rational.of(-3, 5), Rational.of(3, 5).negate());
    }

    @Test
    void inverse() {
        assertThrows(ArithmeticException.class, Rational.ZERO::inverse);
        assertThrows(ArithmeticException.class, Rational.APPROX_ZERO::inverse);
        assertThrows(ArithmeticException.class, () -> Rational.of(0, 7).inverse());
        assertThrows(ArithmeticException.class, () -> Rational.of(0, -7).inverse());

        assertSame(Rational.ONE, Rational.ONE.inverse());
        assertSame(Rational.APPROX_ONE, Rational.APPROX_ONE.inverse());

        assertEquals(Rational.of(7, 3), Rational.of(3, 7).inverse());
        assertEquals(Rational.of(-7, 3), Rational.of(-3, 7).inverse());
    }

    @Test
    void abs() {
        assertSame(Rational.ZERO, Rational.ZERO.abs());
        assertSame(Rational.ONE, Rational.ONE.abs());
        assertSame(Rational.APPROX_ZERO, Rational.APPROX_ZERO.abs());
        assertSame(Rational.APPROX_ONE, Rational.APPROX_ONE.abs());

        assertEquals(Rational.of(3, 7), Rational.of(3, 7).abs());
        assertEquals(Rational.of(3, 7), Rational.of(-3, 7).abs());
        assertEquals(Rational.of(3, 7), Rational.of(3, -7).abs());
    }
}
