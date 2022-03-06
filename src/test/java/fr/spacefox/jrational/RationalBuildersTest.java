package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class RationalBuildersTest {
    private static final String DOUBLE_MAX_VALUE_AS_STRING = "179769313486231570814527423731704356798070567525844"
            + "9965989174768031572607800285387605895586327668781715404589535143824642343213268894641827684675467035"
            + "3751698604991057655128207624549009038932894407586850845513394230458323690322294816580855933212334827"
            + "4797826204144723168738177180919299881250404026184124858368";
    private static final String DOUBLE_MIN_VALUE_DENOMINATOR_AS_STRING = "202402253307310618352495346718917307049"
            + "5566497641421183569013580274303395679953468919603837014371244951870778643168119113898087373857934768"
            + "6701339994073850992151742427656636136446690774209321634123976767847274506856200748342469269861810335"
            + "5649159556340810056512358769552333414615230502532186327508646006263307707741093494784";
    private static final String DOUBLE_MIN_NOMAL_DENOMINATOR_AS_STRING = "449423283715578976932326297697256183404"
            + "4942447355766431835752028943316895137524078317711933060188400528002846996784833941469744220360415562"
            + "3211857659868531094441973356216371319075554900311523529863270738021251442209537670585615720368478277"
            + "635206809290837627671146574559986811484619929076208839082406056034304";

    @Test
    void bigIntegerOk() {
        Rational actual = Rational.of(BigInteger.ONE, BigInteger.TWO);
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TWO, actual.getDenominator());
    }

    @Test
    void bigIntegerFails() {
        assertThrows(ArithmeticException.class, () -> Rational.of(BigInteger.ONE, BigInteger.ZERO));
    }

    @Test
    void longsOk() {
        Rational actual = Rational.of(1L, 2L);
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TWO, actual.getDenominator());
    }

    @Test
    void longsFails() {
        assertThrows(ArithmeticException.class, () -> Rational.of(1L, 0L));
    }

    @Test
    void integersOk() {
        Rational actual = Rational.of(1, 2);
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TWO, actual.getDenominator());
    }

    @Test
    void integersFails() {
        assertThrows(ArithmeticException.class, () -> Rational.of(1, 0));
    }

    @Test
    void longObjectsOk() {
        Rational actual = Rational.of(Long.valueOf(1), Long.valueOf(2L));
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TWO, actual.getDenominator());
    }

    @Test
    void longObjectsFails() {
        assertThrows(ArithmeticException.class, () -> Rational.of(Long.valueOf(1L), Long.valueOf((0L))));
    }

    @Test
    void integerObjectsOk() {
        Rational actual = Rational.of(Integer.valueOf(1), Integer.valueOf(2));
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TWO, actual.getDenominator());
    }

    @Test
    void integerObjectsFails() {
        assertThrows(ArithmeticException.class, () -> Rational.of(Integer.valueOf(1), Integer.valueOf(0)));
    }

    @Test
    void largeValues() {
        Rational actual = Rational.of(
                BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.TWO),
                BigInteger.valueOf(Long.MIN_VALUE).multiply(BigInteger.valueOf(Long.MAX_VALUE)));
        assertEquals(new BigInteger("-9223372036854775809"), actual.getNumerator());
        assertEquals(new BigInteger("85070591730234615856620279821087277056"), actual.getDenominator());
    }

    @Test
    void zero() {
        assertSame(Rational.ZERO, Rational.of(0, 1));
        assertSame(Rational.ZERO, Rational.of(0, Long.MAX_VALUE));
        assertSame(Rational.ZERO, Rational.of(0, Long.MIN_VALUE));
    }

    @Test
    void one() {
        assertSame(Rational.ONE, Rational.of(1, 1));
        assertSame(Rational.ONE, Rational.of(-1, -1));
    }

    @Test
    void signs() {
        Rational actual = Rational.of(1, 10);
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TEN, actual.getDenominator());

        actual = Rational.of(-1, 10);
        assertEquals(BigInteger.valueOf(-1), actual.getNumerator());
        assertEquals(BigInteger.TEN, actual.getDenominator());

        actual = Rational.of(1, -10);
        assertEquals(BigInteger.valueOf(-1), actual.getNumerator());
        assertEquals(BigInteger.TEN, actual.getDenominator());

        actual = Rational.of(-1, -10);
        assertEquals(BigInteger.ONE, actual.getNumerator());
        assertEquals(BigInteger.TEN, actual.getDenominator());
    }

    @Test
    void integers() {
        Rational actual = Rational.of(BigInteger.ZERO);
        assertSame(Rational.ZERO, actual);

        actual = Rational.of(BigInteger.ONE);
        assertSame(Rational.ONE, actual);

        actual = Rational.of(BigInteger.valueOf(1234567890));
        assertEquals(BigInteger.valueOf(1234567890), actual.getNumerator());
        assertEquals(BigInteger.ONE, actual.getDenominator());

        actual = Rational.of(1234567890);
        assertEquals(BigInteger.valueOf(1234567890), actual.getNumerator());
        assertEquals(BigInteger.ONE, actual.getDenominator());
    }

    @Test
    void bigDecimals() {
        Rational actual = Rational.of(BigDecimal.ZERO);
        assertSame(Rational.ZERO, actual);

        actual = Rational.of(new BigDecimal("0.00000"));
        assertSame(Rational.ZERO, actual);

        actual = Rational.of(BigDecimal.ONE);
        assertSame(Rational.ONE, actual);

        actual = Rational.of(new BigDecimal("1.00000000"));
        assertSame(Rational.ONE, actual);

        actual = Rational.of(new BigDecimal("12.347"));
        assertEquals(BigInteger.valueOf(12_347), actual.getNumerator());
        assertEquals(BigInteger.valueOf(1000), actual.getDenominator());

        actual = Rational.of(new BigDecimal("12347000"));
        assertEquals(BigInteger.valueOf(12_347_000), actual.getNumerator());
        assertEquals(BigInteger.ONE, actual.getDenominator());

        actual = Rational.of(new BigDecimal("0.0012347000"));
        assertEquals(BigInteger.valueOf(12_347_000), actual.getNumerator());
        assertEquals(BigInteger.valueOf(10_000_000_000L), actual.getDenominator());
        actual = actual.canonicalForm();
        assertEquals(BigInteger.valueOf(12_347), actual.getNumerator());
        assertEquals(BigInteger.valueOf(10_000_000), actual.getDenominator());
    }

    @Test
    void doubles() {
        // Non convertible values
        assertThrows(ArithmeticException.class, () -> Rational.of(Double.POSITIVE_INFINITY));
        assertThrows(ArithmeticException.class, () -> Rational.of(Double.NEGATIVE_INFINITY));
        assertThrows(ArithmeticException.class, () -> Rational.of(Double.NaN));

        // Special values
        Rational actual = Rational.of(0.0d);
        assertSame(Rational.ZERO, actual);

        actual = Rational.of(1.0d);
        assertSame(Rational.ONE, actual);

        // Conversions that works as naively expected
        actual = Rational.of(0.5d);
        assertEquals(Rational.of(1, 2), actual);
        actual = Rational.of(0.25d);
        assertEquals(Rational.of(1, 4), actual);
        actual = Rational.of(0.125d);
        assertEquals(Rational.of(1, 8), actual);
        actual = Rational.of(2.0d);
        assertEquals(Rational.of(2), actual);
        actual = Rational.of(3.0d);
        assertEquals(Rational.of(3), actual);
        actual = Rational.of(Double.MAX_VALUE);
        assertEquals(Rational.of(DOUBLE_MAX_VALUE_AS_STRING), actual);
        actual = Rational.of(Double.MIN_VALUE);
        assertEquals(Rational.of("1", DOUBLE_MIN_VALUE_DENOMINATOR_AS_STRING), actual);
        actual = Rational.of(Double.MIN_NORMAL);
        assertEquals(Rational.of("1", DOUBLE_MIN_NOMAL_DENOMINATOR_AS_STRING), actual);

        // Conversions that goes to unexpected value due to IEEE-754
        actual = Rational.of(0.1d);
        assertNotEquals(Rational.of(1, 10), actual);
        assertEquals(Rational.of(3602879701896397L, 36028797018963968L), actual);
    }

    @Test
    void floats() {
        // Non convertible values
        assertThrows(ArithmeticException.class, () -> Rational.of(Float.POSITIVE_INFINITY));
        assertThrows(ArithmeticException.class, () -> Rational.of(Float.NEGATIVE_INFINITY));
        assertThrows(ArithmeticException.class, () -> Rational.of(Float.NaN));

        // Special values
        Rational actual = Rational.of(0.0f);
        assertSame(Rational.ZERO, actual);

        actual = Rational.of(1.0f);
        assertSame(Rational.ONE, actual);

        // Conversions that works as naively expected
        actual = Rational.of(0.5f);
        assertEquals(Rational.of(1, 2), actual);
        actual = Rational.of(0.25f);
        assertEquals(Rational.of(1, 4), actual);
        actual = Rational.of(0.125f);
        assertEquals(Rational.of(1, 8), actual);
        actual = Rational.of(2.0f);
        assertEquals(Rational.of(2), actual);
        actual = Rational.of(3.0f);
        assertEquals(Rational.of(3), actual);
        actual = Rational.of(Float.MAX_VALUE);
        assertEquals(Rational.of("340282346638528859811704183484516925440"), actual);
        actual = Rational.of(Float.MIN_VALUE);
        assertEquals(Rational.of("1", "713623846352979940529142984724747568191373312"), actual);
        actual = Rational.of(Float.MIN_NORMAL);
        assertEquals(Rational.of("1", "85070591730234615865843651857942052864"), actual);

        // Conversions that goes to unexpected value due to IEEE-754
        actual = Rational.of(0.1f);
        assertNotEquals(Rational.of(1, 10), actual);
        assertEquals(Rational.of(13421773, 134217728), actual);
    }

    @Test
    void canonicalForm() {
        Rational actual = Rational.of(3, 5);
        assertEquals(BigInteger.valueOf(3), actual.getNumerator());
        assertEquals(BigInteger.valueOf(5), actual.getDenominator());
        assertSame(actual, actual.canonicalForm());

        actual = Rational.of(12, 20);
        assertEquals(BigInteger.valueOf(12), actual.getNumerator());
        assertEquals(BigInteger.valueOf(20), actual.getDenominator());
        actual = actual.canonicalForm();
        assertEquals(BigInteger.valueOf(3), actual.getNumerator());
        assertEquals(BigInteger.valueOf(5), actual.getDenominator());

        actual = Rational.of(42, -390);
        assertEquals(BigInteger.valueOf(-42), actual.getNumerator());
        assertEquals(BigInteger.valueOf(390), actual.getDenominator());
        actual = actual.canonicalForm();
        assertEquals(BigInteger.valueOf(-7), actual.getNumerator());
        assertEquals(BigInteger.valueOf(65), actual.getDenominator());
    }
}
