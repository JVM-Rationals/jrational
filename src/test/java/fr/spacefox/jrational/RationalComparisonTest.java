package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RationalComparisonTest {

    @Nested
    class CompareToTest {
        @Test
        void nullTest() {
            assertThrows(NullPointerException.class, () -> Rational.ONE.compareTo(null));
        }

        @Test
        void zeros() {
            assertEquals(0, Rational.ZERO.compareTo(Rational.of(0)));
            assertEquals(0, Rational.APPROX_ZERO.compareTo(Rational.ZERO));
            assertEquals(0, Rational.ZERO.compareTo(Rational.APPROX_ZERO));
        }

        @Test
        void equalities() {
            assertEquals(0, Rational.ONE.compareTo(Rational.of(1)));
            assertEquals(0, Rational.ONE.compareTo(Rational.of(10, 10)));
            assertEquals(0, Rational.of(-1, 1).compareTo(Rational.of(-2, 2)));
        }

        @Test
        void sameDenominator() {
            assertEquals(-1, Rational.of(1, 3).compareTo(Rational.of(2, 3)));
            assertEquals(-1, Rational.of(2, 3).compareTo(Rational.of(4, 3)));
            assertEquals(-1, Rational.of(4, 3).compareTo(Rational.of(5, 3)));

            assertEquals(1, Rational.of(2, 3).compareTo(Rational.of(1, 3)));
            assertEquals(1, Rational.of(4, 3).compareTo(Rational.of(2, 3)));
            assertEquals(1, Rational.of(5, 3).compareTo(Rational.of(4, 3)));

            assertEquals(-1, Rational.of(-7, 4).compareTo(Rational.of(3, 4)));
            assertEquals(1, Rational.of(3, 4).compareTo(Rational.of(-7, 4)));
            assertEquals(-1, Rational.of(7, -4).compareTo(Rational.of(-3, -4)));
            assertEquals(1, Rational.of(-3, -4).compareTo(Rational.of(7, -4)));
        }

        @Test
        void differentDenominators() {
            assertEquals(-1, Rational.of(2, 5).compareTo(Rational.of(3, 4)));
            assertEquals(1, Rational.of(7, 5).compareTo(Rational.of(3, 4)));
        }
    }

    // See Comparable#compareTo()
    @Nested
    class CompareToApiConsistencyTest {
        @Test
        void signConsistency() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 5);
            assertEquals(x.compareTo(y), -y.compareTo(x));
        }

        @Test
        void transitivity() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 5);
            final Rational z = Rational.of(7, 8);
            assertEquals(-1, x.compareTo(y));
            assertEquals(-1, y.compareTo(z));
            assertEquals(-1, x.compareTo(z));
        }

        @Test
        void equalities() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 6);
            final Rational z = Rational.of(7, 8);
            assertEquals(0, x.compareTo(y));
            assertEquals(x.compareTo(z), y.compareTo(z));
        }
    }

    @Nested
    class EqualsTest {
        @Test
        void sameObject() {
            final Rational r = Rational.of(1, 2);
            assertEquals(r, r);
            assertEquals(Rational.APPROX_ZERO, Rational.APPROX_ZERO);
            assertEquals(Rational.APPROX_ONE, Rational.APPROX_ONE);
        }

        @Test
        void incorrectObject() {
            assertNotEquals(null, Rational.of(1, 2));
            assertNotEquals(new Object(), Rational.of(3, 4));
            assertNotEquals(Rational.of(3, 4), new Object());
        }

        @Test
        void sameValueDifferentRepresentations() {
            assertEquals(Rational.of(1, 2), Rational.of(10, 20));
            assertEquals(Rational.of(1, 2), Rational.of(-1, -2));
        }

        @Test
        void approximateEquality() {
            assertNotEquals(Rational.approximateOf(1, 2), Rational.of(1, 2));
            assertNotEquals(Rational.approximateOf(3, 4), Rational.approximateOf(3, 4));
            final Rational r = Rational.approximateOf(5, 6);
            assertEquals(r, r);
        }
    }

    // See Object#equals()
    @Nested
    class EqualsApiConsistencyTest {
        @Test
        void reflective() {
            final Rational r = Rational.of(22, 7);
            assertEquals(r, r);
        }

        @Test
        void symmetric() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 6);
            assertEquals(x, y);
            assertEquals(y, x);
        }

        @Test
        void transitive() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 6);
            final Rational z = Rational.of(10, 15);
            assertEquals(x, y);
            assertEquals(y, z);
            assertEquals(x, z);
        }

        @Test
        void consistent() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 6);
            assertEquals(x, y);
            assertEquals(x, y);
        }

        @Test
        void nullEqualityReturnsFalse() {
            assertNotEquals(null, Rational.of(22, 7));
        }
    }

    // a.equals(b) implies a.hashCode() == b.hashCode()
    @Nested
    class EqualsAndHashCodeConsistencyTest {
        @Test
        void zero() {
            assertEquals(0, Rational.ZERO.hashCode());
        }

        @Test
        void smallerThanOnePositive() {
            final Rational x = Rational.of(2, 3);
            final Rational y = Rational.of(4, 6);
            assertEquals(x, y);
            assertEquals(x.hashCode(), y.hashCode());
        }

        @Test
        void smallerThanOneNegative() {
            final Rational x = Rational.of(-2, 3);
            final Rational y = Rational.of(4, -6);
            assertEquals(x, y);
            assertEquals(x.hashCode(), y.hashCode());
        }

        @Test
        void biggerThanOnePositive() {
            final Rational x = Rational.of(20, 3);
            final Rational y = Rational.of(40, 6);
            assertEquals(x, y);
            assertEquals(x.hashCode(), y.hashCode());
        }

        @Test
        void biggerThanOneNegative() {
            final Rational x = Rational.of(-20, 3);
            final Rational y = Rational.of(40, -6);
            assertEquals(x, y);
            assertEquals(x.hashCode(), y.hashCode());
        }

        @Test
        void unequalObjectsShouldProduceDifferentHashCodes() {
            assertNotEquals(Rational.of(1, 2), Rational.of(3, 4));
            assertNotEquals(Rational.of(10, 2), Rational.of(30, 4));
            assertNotEquals(Rational.of(-1, 2), Rational.of(3, -4));
            assertNotEquals(Rational.of(10, -2), Rational.of(-30, 4));
        }
    }
}
