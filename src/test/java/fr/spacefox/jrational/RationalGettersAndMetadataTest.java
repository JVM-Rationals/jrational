package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RationalGettersAndMetadataTest {

    @Test
    void isApproximate() {
        assertFalse(Rational.ZERO.isApproximate());
        assertFalse(Rational.ONE.isApproximate());

        assertTrue(Rational.APPROX_ZERO.isApproximate());
        assertTrue(Rational.APPROX_ONE.isApproximate());

        assertFalse(Rational.of(10, 42).isApproximate());
        assertTrue(Rational.approximateOf(10, 42).isApproximate());
    }

    @Test
    void isInteger() {
        assertTrue(Rational.of(10).isInteger());
        assertTrue(Rational.of(70, 7).isInteger());
        assertTrue(Rational.of(-70, 7).isInteger());
        assertTrue(Rational.of(70, -7).isInteger());
        assertTrue(Rational.of(-70, -7).isInteger());

        assertFalse(Rational.of(70, 8).isInteger());
        assertFalse(Rational.of(-70, 8).isInteger());
        assertFalse(Rational.of(70, -8).isInteger());
        assertFalse(Rational.of(-70, -8).isInteger());
    }

    @Test
    void signum() {
        assertEquals(-1, Rational.of(-22, 7).signum());
        assertEquals(-1, Rational.of(22, -7).signum());

        assertEquals(0, Rational.ZERO.signum());
        assertEquals(0, Rational.of(0, 1234).signum());

        assertEquals(1, Rational.of(22, 7).signum());
        assertEquals(1, Rational.of(-22, -7).signum());
    }
}
