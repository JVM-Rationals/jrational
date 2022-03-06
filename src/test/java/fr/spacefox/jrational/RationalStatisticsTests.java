package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RationalStatisticsTests {

    @Test
    void min() {
        final Rational a = Rational.of(1, 2);
        final Rational b = Rational.of(1, 3);
        final Rational c = Rational.of(2, 4);

        assertSame(b, a.min(b));
        assertSame(b, b.min(a));
        assertSame(b, c.min(b));
        assertSame(b, b.min(c));
        assertSame(a, a.min(c));
        assertSame(c, c.min(a));

        assertThrows(ArithmeticException.class, () -> Rational.min(Collections.emptyList()));
        assertThrows(ArithmeticException.class, () -> Rational.min());

        assertSame(b, Rational.min(List.of(a, b, c)));
        assertSame(b, Rational.min(a, b, c));
    }

    @Test
    void max() {
        final Rational a = Rational.of(1, 2);
        final Rational b = Rational.of(1, 3);
        final Rational c = Rational.of(2, 4);

        assertSame(a, a.max(b));
        assertSame(a, b.max(a));
        assertSame(c, c.max(b));
        assertSame(c, b.max(c));
        assertSame(a, a.max(c));
        assertSame(c, c.max(a));

        assertThrows(ArithmeticException.class, () -> Rational.max(Collections.emptyList()));
        assertThrows(ArithmeticException.class, () -> Rational.max());

        assertSame(a, Rational.max(List.of(a, b, c)));
        assertSame(a, Rational.max(a, b, c));
    }

    @Test
    void average() {
        assertThrows(ArithmeticException.class, () -> Rational.average(Collections.emptyList()));

        assertEquals(Rational.of(22, 7), Rational.average(List.of(Rational.of(22, 7))));
        assertEquals(Rational.of(3, 8), Rational.average(List.of(Rational.of(1, 2), Rational.of(1, 4))));
        assertEquals(Rational.ZERO, Rational.average(List.of(Rational.of(-5, 6), Rational.of(500, 600))));
    }

    @Test
    void median() {
        assertThrows(ArithmeticException.class, () -> Rational.median(Collections.emptyList()));

        assertEquals(Rational.of(22, 7), Rational.median(List.of(Rational.of(22, 7))));

        assertEquals(
                Rational.of(1, 3), Rational.median(List.of(Rational.of(1, 2), Rational.of(1, 4), Rational.of(1, 3))));
        assertEquals(
                Rational.of(7, 24),
                Rational.median(List.of(Rational.of(1, 5), Rational.of(1, 2), Rational.of(1, 4), Rational.of(1, 3))));
    }
}
