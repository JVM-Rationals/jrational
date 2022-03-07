package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class RationalToolingTest {

    private static final Rational PI1000DIGITS = Rational.of("3.14159265358979323846264338327950288419716939937510582"
                    + "09749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174"
                    + "50284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648566923"
                    + "46034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133"
                    + "05305488204665213841469519415116094330572703657595919530921861173819326117931051185480744623799627495673"
                    + "51885752724891227938183011949129833673362440656643086021394946395224737190702179860943702770539217176293"
                    + "17675238467481846766940513200056812714526356082778577134275778960917363717872146844090122495343014654958"
                    + "53710507922796892589235420199561121290219608640344181598136297747713099605187072113499999983729780499510"
                    + "59731732816096318595024459455346908302642522308253344685035261931188171010003137838752886587533208381420"
                    + "61717766914730359825349042875546873115956286388235378759375195778185778053217122680661300192787661119590"
                    + "9216420198")
            .canonicalForm();

    @Test
    void magnitude() {
        assertEquals(1, Rational.ZERO.magnitude());
        assertEquals(2, Rational.ONE.magnitude());
        assertEquals(1, Rational.of(-1).magnitude());

        for (int i = 2; i <= 256; i++) {
            final BigInteger twoPowI = BigInteger.TWO.pow(i);
            final BigInteger twoPowIMinusOne = twoPowI.subtract(BigInteger.ONE);
            // Positive numbers
            assertEquals(i + 1, Rational.of(twoPowIMinusOne).magnitude());
            assertEquals(i + 1, Rational.of(BigInteger.ONE, twoPowIMinusOne).magnitude());
            assertEquals(i + 2, Rational.of(twoPowI).magnitude());
            assertEquals(i + 2, Rational.of(BigInteger.ONE, twoPowI).magnitude());
            assertEquals(2 * i + 1, Rational.of(twoPowIMinusOne, twoPowI).magnitude());
            assertEquals(2 * i + 1, Rational.of(twoPowI, twoPowIMinusOne).magnitude());
            // Negative numbers
            assertEquals(i + 1, Rational.of(twoPowIMinusOne.negate()).magnitude());
            assertEquals(
                    i, Rational.of(BigInteger.ONE.negate(), twoPowIMinusOne).magnitude());
            assertEquals(i + 1, Rational.of(twoPowI.negate()).magnitude());
            assertEquals(i + 1, Rational.of(BigInteger.ONE.negate(), twoPowI).magnitude());
            assertEquals(
                    2 * i + 1, Rational.of(twoPowIMinusOne.negate(), twoPowI).magnitude());
            assertEquals(2 * i, Rational.of(twoPowI.negate(), twoPowIMinusOne).magnitude());
        }
    }

    @Test
    void approximate() {
        assertThrows(ArithmeticException.class, () -> Rational.PI.approximate(BigInteger.ZERO));
        assertThrows(ArithmeticException.class, () -> Rational.PI.approximate(-42));

        assertNotEquals(0, Rational.PI.compareTo(PI1000DIGITS));
        assertEquals(0, Rational.PI.compareTo(PI1000DIGITS.approximate()));

        assertEquals(0, Rational.of(355, 113).compareTo(Rational.PI.approximate(113)));
        assertEquals(0, Rational.of(22, 7).compareTo(Rational.PI.approximate(7)));
        assertEquals(0, Rational.of(3).compareTo(Rational.PI.approximate(1)));
        assertEquals(Math.PI, Rational.PI.doubleValue());

        final Rational a = Rational.of(1_000_000_001L, 1_000_000_000L);
        assertSame(a, a.approximate(10_000_000_000L));
        assertSame(a, a.approximate(1_000_000_000L));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(100_000_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(10_000_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(1_000_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(100_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(10_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(1_000)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(100)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(10)));
        assertEquals(0, Rational.ONE.compareTo(a.approximate(1)));
    }

    @Test
    void approximateAsExactValue() {
        final Rational longRepresentation = Rational.of(500_000, 100_000);
        final Rational shortRepresentation = Rational.of(5);
        final Rational approximate = longRepresentation.approximate(10);
        assertNotSame(shortRepresentation, approximate);
        assertEquals(shortRepresentation, approximate);
        assertFalse(approximate.isApproximate());
    }

    @Test
    void approximateRoundsNearest() {
        // x.xxxx[6-9] -> ceil rounding
        assertEquals(0, Rational.of(13, 10).compareTo(Rational.of(129, 100).approximate(10)));
        assertEquals(0, Rational.of(13, 10).compareTo(Rational.of(128, 100).approximate(10)));
        assertEquals(0, Rational.of(13, 10).compareTo(Rational.of(127, 100).approximate(10)));
        assertEquals(0, Rational.of(13, 10).compareTo(Rational.of(126, 100).approximate(10)));
        // x.xxxx5 -> floor rounding
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(125, 100).approximate(10)));
        // x.xxxx[0-4] -> floor rounding
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(124, 100).approximate(10)));
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(123, 100).approximate(10)));
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(122, 100).approximate(10)));
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(121, 100).approximate(10)));
        assertEquals(0, Rational.of(12, 10).compareTo(Rational.of(120, 100).approximate(10)));
    }

    @Test
    void toStringTest() {
        assertEquals("0", Rational.ZERO.toString());
        assertEquals("0", Rational.ZERO.toString()); // Second call don’t change anything
        assertEquals("1", Rational.ONE.toString());

        assertEquals("~0", Rational.APPROX_ZERO.toString());
        assertEquals("~1", Rational.APPROX_ONE.toString());

        assertEquals("3", Rational.of(3).toString());
        assertEquals("9/3", Rational.of(9, 3).toString()); // No automatic canonic form

        assertEquals("3/7", Rational.of(3, 7).toString());
        assertEquals("3/7", Rational.of(-3, -7).toString());
        assertEquals("-3/7", Rational.of(-3, 7).toString());
        assertEquals("-3/7", Rational.of(3, -7).toString());

        assertEquals("~3/7", Rational.approximateOf(3, 7).toString());
        assertEquals("~3/7", Rational.approximateOf(-3, -7).toString());
        assertEquals("~-3/7", Rational.approximateOf(-3, 7).toString());
        assertEquals("~-3/7", Rational.approximateOf(3, -7).toString());
    }
}
