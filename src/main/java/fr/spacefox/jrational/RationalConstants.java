package fr.spacefox.jrational;

import java.math.BigInteger;

/**
 * Some constants expressed as a {@code Rational} with enough precision to not be modified by {@code approximate()}.
 */
public final class RationalConstants {

    /**
     * The {@code Rational} value that is closer than any other to pi, the ratio of the circumference of a circle to its
     * diameter, with enough precision to not be modified by {@code approximate()} method, canonical form.
     *
     * Value is {@code 1069028584064966747859680373161870783301/340282366920938463463374607431768211456}
     */
    public static final Rational PI = Rational.approximateOf(
            new BigInteger("1069028584064966747859680373161870783301"),
            new BigInteger("340282366920938463463374607431768211456"));

    /**
     * The {@code Rational} value that is closer than any other to e, the base of the natural logarithms, with enough
     * precision to not be modified by {@code approximate()} method, canonical form.
     *
     * Value is {@code 924983374546220337150911035843336795079/340282366920938463463374607431768211456}
     */
    public static final Rational E = Rational.approximateOf(
            new BigInteger("924983374546220337150911035843336795079"),
            new BigInteger("340282366920938463463374607431768211456"));

    private RationalConstants() {
        // Constant class cannot be instanced
    }
}
