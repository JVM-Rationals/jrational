package fr.spacefox.jrational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class RationalVsIee754Test {

    /**
     * The classic : 0.1 + 0.2 = 0.3 (false with float or double as none of these numbers has an exact representation)
     */
    @Test
    void simpleSum() {
        assertEquals(Rational.of(3, 10), Rational.of(1, 10).add(Rational.of(2, 10)));
    }

    /**
     * Based on this code:
     * <pre>
     * {@code
     * double b = 4095.1;
     * double a = b + 1;
     * double x = 1;
     *
     * for (int i = 0; i < 100; ++i) {
     *     x = (a * x) - b;
     *     System.out.println("Iter " + i + " - " + x);
     * }
     * }
     * </pre>
     * That produces:
     * <pre>
     * Iter 0 - 1.0000000000004547
     * Iter 1 - 1.0000000018631
     * Iter 2 - 1.0000076314440776
     * Iter 3 - 1.0312591580864137
     * Iter 4 - 129.04063743775941
     * Iter 5 - 524468.2550088064
     * Iter 6 - 2.148270324241572E9
     * Iter 7 - 8.799530071030805E12
     * [...]
     * Iter 87 - 8.592183395613292E301
     * Iter 88 - 3.519444240677161E305
     * Iter 89 - Infinity
     * Iter 90 - Infinity
     * </pre>
     */
    @Test
    void errorPropagation() {
        final Rational b = Rational.of(40951, 10);
        final Rational a = b.add(Rational.ONE);
        Rational x = Rational.ONE;
        final Rational expected = Rational.ONE;

        for (int i = 0; i < 100; i++) {
            x = (a.multiply(x)).subtract(b);
            assertEquals(expected, x);
        }
    }

    /**
     * Compute Riemann series, two ways.
     * With float or double, both ways gives different values due to IEEE-754 limitations (lack of associativity).
     * @see <a href="https://zestedesavoir.com/billets/2149/comment-reordonner-une-somme-de-flottants-peut-changer-son-resultat/">...</a>
     * (in french).
     * With rationals, both ways should give the same result.
     */
    @Test
    void associativity() {
        Rational sumFromBig = Rational.ZERO;
        Rational ri;
        for (int i = 1; i <= 1000; i++) {
            ri = Rational.of(i);
            sumFromBig = sumFromBig.add(Rational.ONE.divide(ri.multiply(ri)));
        }

        Rational sumFromSmall = Rational.ZERO;
        for (int i = 1000; i >= 1; i--) {
            ri = Rational.of(i);
            sumFromSmall = sumFromSmall.add(Rational.ONE.divide(ri.multiply(ri)));
        }

        assertEquals(sumFromSmall, sumFromBig);
    }

    /**
     * <pre>
     * {@code
     * double a = 1e-10;
     * double b = 9e307;
     * double c = 9e307;
     * System.out.println(a * (b + c));
     * System.out.println((a * b) + (a * c));
     * }
     * </pre>
     * Gives
     * <pre>
     * Infinity
     * 1.8000000000000002E298
     * </pre>
     * And none of these values are exact.
     * Rationals must give exact value in both cases.
     */
    @Test
    void handleLargeValues() {
        final Rational a = Rational.of(BigInteger.ONE, BigInteger.TEN.pow(10));
        final Rational b = Rational.of(BigInteger.valueOf(9).multiply(BigInteger.TEN.pow(307)));
        final Rational c = Rational.of(BigInteger.valueOf(9).multiply(BigInteger.TEN.pow(307)));
        final Rational direct = a.multiply(b.add(c));
        final Rational split = (a.multiply(b)).add(a.multiply(c));
        final Rational expected = Rational.of(BigInteger.valueOf(18).multiply(BigInteger.TEN.pow(297)));

        assertEquals(expected, direct);
        assertEquals(expected, split);
        assertEquals(direct, split);
    }

    /**
     * The Müller and Kahan recurrence should converge to 6, but with IEEE-754 float types, it converges to 100:
     * <pre>
     * {@code
     * double x0 = 11.0 / 2.0;
     * double x1 = 61.0 / 11.0;
     *
     * for (int i = 0; i < 100; i++) {
     *     double x2 = 111.0 - ((1130.0 - (3000.0 / x0)) / x1);
     *     x0 = x1;
     *     x1 = x2;
     *     System.out.println(x2);
     * }
     * }
     * </pre>
     * Gives :
     * <pre>
     * 5.5901639344262435
     * 5.633431085044251
     * 5.674648620514802
     * 5.713329052462441
     * 5.74912092113604
     * 5.781810945409518
     * 5.81131466923334
     * 5.83766396240722
     * 5.861078484508624
     * 5.883542934069212
     * 5.935956716634138
     * 6.534421641135182
     * 15.413043180845833
     * 67.47239836474625
     * 97.13715118465481
     * 99.82469414672073
     * 99.98953968869486
     * 99.9993761416421
     * 99.99996275956511
     * 99.99999777513808
     * 99.99999986698653
     * 99.9999999920431
     * 99.99999999952378
     * 99.9999999999715
     * 99.9999999999983
     * 99.9999999999999
     * 100.0
     * 100.0
     * [...]
     * </pre>
     */
    @Test
    void mullerAndKahanRecurrence() {
        final Rational limit = Rational.of(6);

        Rational x0 = Rational.of(11, 2);
        Rational x1 = Rational.of(61, 11);

        Rational lastDelta = null;

        for (int i = 0; i < 100; i++) {
            final Rational x2 = Rational.of(111)
                    .subtract(((Rational.of(1130).subtract(Rational.of(3000).divide(x0)))).divide(x1))
                    .canonicalForm();
            x0 = x1;
            x1 = x2;

            // Computed value x2 tends converge to 6
            final Rational delta = limit.subtract(x2);
            assertTrue(delta.lt(Rational.ONE));
            if (lastDelta != null) {
                assertTrue(delta.lt(lastDelta));
            }
            lastDelta = delta;
        }
    }

    private static final Random RANDOM = new Random();

    private static String stringNumberOfLength(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(RANDOM.nextInt(10)))
                .collect(Collectors.joining());
    }
}
