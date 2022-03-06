package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RationalComparisonHelpersTest {

    @Test
    void gt() {
        assertTrue(Rational.of(1, 2).gt(Rational.of(1, 4)));
        assertFalse(Rational.of(1, 2).gt(Rational.of(2, 4)));
        assertFalse(Rational.of(1, 2).gt(Rational.of(3, 4)));
    }

    @Test
    void ge() {
        assertTrue(Rational.of(1, 2).ge(Rational.of(1, 4)));
        assertTrue(Rational.of(1, 2).ge(Rational.of(2, 4)));
        assertFalse(Rational.of(1, 2).ge(Rational.of(3, 4)));
    }

    @Test
    void lt() {
        assertFalse(Rational.of(1, 2).lt(Rational.of(1, 4)));
        assertFalse(Rational.of(1, 2).lt(Rational.of(2, 4)));
        assertTrue(Rational.of(1, 2).lt(Rational.of(3, 4)));
    }

    @Test
    void le() {
        assertFalse(Rational.of(1, 2).le(Rational.of(1, 4)));
        assertTrue(Rational.of(1, 2).le(Rational.of(2, 4)));
        assertTrue(Rational.of(1, 2).le(Rational.of(3, 4)));
    }
}
