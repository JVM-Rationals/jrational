package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class RationalBasicArithmeticTest {
    @Test
    void addIdentity() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 2).add(Rational.ZERO));
        assertEquals(Rational.of(1, 2), Rational.ZERO.add(Rational.of(1, 2)));

        assertEquals(0, Rational.of(1, 2).compareTo(Rational.of(1, 2).add(Rational.APPROX_ZERO)));
        assertEquals(0, Rational.of(1, 2).compareTo(Rational.APPROX_ZERO.add(Rational.of(1, 2))));
    }

    @Test
    void add() {
        assertSame(Rational.ZERO, Rational.of(-7, 5).add(Rational.of(7, 5)));
        assertSame(Rational.ONE, Rational.of(1, 3).add(Rational.of(2, 3)));
        assertEquals(Rational.of(16, 15), Rational.of(2, 5).add(Rational.of(2, 3)));

        // Keep approximations
        Rational actual = Rational.of(12, 13).add(Rational.ONE);
        assertEquals(Rational.of(25, 13), actual);
        assertFalse(actual.isApproximate());

        actual = Rational.of(12, 13).add(Rational.APPROX_ONE);
        assertNotEquals(Rational.of(25, 13), actual);
        assertEquals(0, Rational.of(25, 13).compareTo(actual));
        assertTrue(actual.isApproximate());
    }

    @Test
    void sum() {
        assertSame(Rational.ZERO, Rational.sum(Collections.emptyList()));
        assertEquals(
                Rational.of(1136, 384),
                Rational.sum(List.of(Rational.of(1, 2), Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8))));
        assertEquals(
                Rational.of(1136, 384),
                Rational.sum(Rational.of(1, 2), Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8)));
    }

    @Test
    void addAll() {
        assertEquals(
                Rational.of(1136, 384),
                Rational.of(1, 2).addAll(List.of(Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8))));
        assertEquals(
                Rational.of(1136, 384),
                Rational.of(1, 2).addAll(Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8)));
    }

    @Test
    void substract() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 2).subtract(Rational.ZERO));
        assertSame(Rational.ONE, Rational.of(5, 3).subtract(Rational.of(2, 3)));
        assertSame(Rational.ZERO, Rational.of(7, 5).subtract(Rational.of(7, 5)));
        assertEquals(Rational.of(-4, 15), Rational.of(2, 5).subtract(Rational.of(2, 3)));

        // Keep approximations
        Rational actual = Rational.of(12, 13).subtract(Rational.ONE);
        assertEquals(Rational.of(-1, 13), actual);
        assertFalse(actual.isApproximate());

        actual = Rational.of(12, 13).subtract(Rational.APPROX_ONE);
        assertNotEquals(Rational.of(-1, 13), actual);
        assertEquals(0, Rational.of(-1, 13).compareTo(actual));
        assertTrue(actual.isApproximate());
    }

    @Test
    void multiplyAbsorbingElement() {
        assertSame(Rational.ZERO, Rational.of(1, 2).multiply(Rational.ZERO));
        assertSame(Rational.ZERO, Rational.ZERO.multiply(Rational.of(1, 2)));

        assertSame(Rational.APPROX_ZERO, Rational.of(1, 2).multiply(Rational.APPROX_ZERO));
        assertSame(Rational.APPROX_ZERO, Rational.APPROX_ZERO.multiply(Rational.of(1, 2)));
    }

    @Test
    void multiplyIdentity() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 2).multiply(Rational.ONE));
        assertEquals(Rational.of(1, 2), Rational.ONE.multiply(Rational.of(1, 2)));

        assertEquals(0, Rational.of(1, 2).compareTo(Rational.of(1, 2).multiply(Rational.APPROX_ONE)));
        assertEquals(0, Rational.of(1, 2).compareTo(Rational.APPROX_ONE.multiply(Rational.of(1, 2))));
    }

    @Test
    void multiply() {
        assertSame(Rational.ONE, Rational.of(-7, 5).multiply(Rational.of(-5, 7)));
        assertEquals(Rational.of(3, 8), Rational.of(1, 2).multiply(Rational.of(3, 4)));
    }

    @Test
    void product() {
        assertSame(Rational.ONE, Rational.product(Collections.emptyList()));
        assertEquals(
                Rational.of(105, 384),
                Rational.product(List.of(Rational.of(1, 2), Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8))));
        assertEquals(
                Rational.of(105, 384),
                Rational.product(Rational.of(1, 2), Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8)));
    }

    @Test
    void multiplyAll() {
        assertEquals(
                Rational.of(105, 384),
                Rational.of(1, 2).multiplyAll(List.of(Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8))));
        assertEquals(
                Rational.of(105, 384),
                Rational.of(1, 2).multiplyAll(Rational.of(3, 4), Rational.of(5, 6), Rational.of(7, 8)));
    }

    @Test
    void divideIdentity() {
        assertEquals(Rational.of(1, 2), Rational.of(1, 2).divide(Rational.ONE));
        assertEquals(Rational.of(1, 2), Rational.ONE.divide(Rational.of(1, 2)));

        assertEquals(0, Rational.of(1, 2).compareTo(Rational.of(1, 2).divide(Rational.APPROX_ONE)));
        assertEquals(0, Rational.of(1, 2).compareTo(Rational.APPROX_ONE.divide(Rational.of(1, 2))));
    }

    @Test
    void divide() {
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 2).divide(Rational.ZERO));
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 2).divide(Rational.APPROX_ZERO));

        assertSame(Rational.ZERO, Rational.ZERO.divide(Rational.of(1, 2)));
        assertSame(Rational.APPROX_ZERO, Rational.APPROX_ZERO.divide(Rational.of(1, 2)));

        assertSame(Rational.ONE, Rational.of(-7, 5).divide(Rational.of(-7, 5)));

        assertEquals(Rational.of(4, 6), Rational.of(1, 2).divide(Rational.of(3, 4)));
    }

    @Test
    void powShortcuts() {
        // x^0 = 0 (including 0^0)
        assertSame(Rational.ONE, Rational.ZERO.pow(0));
        assertSame(Rational.ONE, Rational.of(22, 7).pow(0));

        assertSame(Rational.ONE, Rational.ONE.pow(123));
        assertSame(Rational.APPROX_ONE, Rational.APPROX_ONE.pow(123));

        assertEquals(Rational.of(22, 7), Rational.of(22, 7).pow(1));
    }

    @Test
    void pow() {
        assertEquals(Rational.of(10648, 343), Rational.of(22, 7).pow(3));
        assertEquals(Rational.of(343, 10648), Rational.of(22, 7).pow(-3));
    }
}
