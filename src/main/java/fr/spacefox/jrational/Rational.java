package fr.spacefox.jrational;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Immutable rational numbers. Quotient {@code p/q} of two integers, a numerator p and a non-zero denominator q.
 *
 * <p>Internal representation uses two {@code BigInteger} for numerator and denominator. This has the following
 * implications:
 * <ol>
 *     <li>Precision: {@code Rational}’s precision depends on {@code BigInteger} precision, which is arbitrary and
 *     virtually infinite.</li>
 *     <li>Performances: {@code Rational}’s performance highly depends on {@code BigInteger} performances, as most
 *     operation are backed by computations on {@code BigInteger}.</li>
 * </ol>
 *
 * <p>Semantics of arithmetics operation and public API method names also follows {@code BigInteger} ones. Also, an
 * {@code ArithmeticException} is thrown when a builder or a method would attempt an illegal operation.
 *
 * <p><b>Constructors and builders:</b> There is no public constructor for {@code Rational}. The only way to create a
 * {@code Rational} is to use a static builder {@code Rational.of(something)}.
 *
 * <p><b>Limitations:</b> There is only one zero (no positive or negative zero), and special values like infinities and
 * "not a number" cannot be represented with a {@code Rational}.
 *
 * <p><b>Precision and performances:</b> As performances directly depends on underlying {@code BigInteger} arithmetics.
 * This implies the more "precise" the {@code Rational} is, slower it is. Preliminary tests seems the computing
 * complexity is in O(n), with n the size of numerator and denominator. This is important as chained calculus may lead
 * to an irreducible rational which still is a quotient of two very large integers (thousands of decimal figures) then
 * slow to handle, even if the rational number itself isn’t very large or small. The method {@code magnitude()} method
 * will help to detect {@code Rational} backed with large {@code BigInteger}; and the {@code approximate()} and
 * {@code canonicalForm()} methods allows to shrink them to more reasonable numbers.
 *
 * <p><b>Approximate rationals:</b> A {@code Rational} may be <i>approximate</i> (see {@code isApproximate()} method).
 * This denotes this rational is only an approximation of the real value. The real value may be, or not, an irrational
 * number in the mathematical definition. There is no constructor nor builder to create an approximate {@code Rational},
 * as all the builder require exact values. There are three ways to get an approximate {@code Rational}. First is
 * provided constants. Second is to call {@code approximate()} on an existing number, the result will be flagged as
 * approximate if and only if an approximation has been done. The second one is through arithmetic methods that can lead
 * to irrational results. Any operation that implies an approximate {@code Rational} will always have an approximate as
 * result.
 *
 * @apiNote This class has a natural ordering that is inconsistent with equals. Care should be exercised if
 * {@code Rational} objects are used as keys in a {@link java.util.SortedMap SortedMap} or elements in a
 * {@link java.util.SortedSet SortedSet} since {@code Rational}’s natural ordering is inconsistant with equals.
 * See {@link Comparable}, {@link java.util.SortedSet} or {@link java.util.SortedMap} for more information.
 *
 * @see BigInteger
 * @see java.util.SortedMap
 * @see java.util.SortedSet
 * @author SpaceFox
 */
public final class Rational extends Number implements Comparable<Rational> {

    public static final Rational ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE, false);
    public static final Rational ONE = new Rational(BigInteger.ONE, BigInteger.ONE, false);

    public static final Rational APPROX_ZERO = new Rational(BigInteger.ZERO, BigInteger.ONE, true);
    public static final Rational APPROX_ONE = new Rational(BigInteger.ONE, BigInteger.ONE, true);

    /**
     * Approximate form of pi, with enough precision to not be modified by {@code approximate()} method, canonical form.
     */
    public static final Rational PI = new Rational(
            new BigInteger("1069028584064966747859680373161870783301"),
            new BigInteger("340282366920938463463374607431768211456"),
            true);

    // The scale required to handle double values in BigDecimal objects without precision lose (assume rational has been
    // created from double values, therefore conversion can be exact).
    private static final int DOUBLE_REQUIRED_SCALE = 1074; // = new BigDecimal(Double.MIN_VALUE).scale();

    private final @NotNull BigInteger numerator;
    private final @NotNull BigInteger denominator;
    private final boolean approximate;
    private String stringCache;

    // region Constructor and builders
    private Rational(
            final @NotNull BigInteger numerator, final @NotNull BigInteger denominator, final boolean approximate) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.approximate = approximate;
    }

    private static Rational of(
            final @NotNull BigInteger numerator, final @NotNull BigInteger denominator, final boolean approximate) {
        if (BigInteger.ZERO.equals(denominator)) {
            throw new ArithmeticException("Denominator can’t be 0.");
        }

        if (BigInteger.ZERO.equals(numerator)) {
            return approximate ? APPROX_ZERO : ZERO;
        }
        if (numerator.equals(denominator)) {
            return approximate ? APPROX_ONE : ONE;
        }

        // Handle signs:

        // Numerator and denominator can’t be 0
        // -> no need to handle .signum() = 0
        final boolean positive = (numerator.signum() * denominator.signum()) == 1;

        // Numerator handles the sign
        final BigInteger realNum;
        if (positive) {
            realNum = numerator.signum() == 1 ? numerator : numerator.negate();
        } else {
            realNum = numerator.signum() == 1 ? numerator.negate() : numerator;
        }
        // Denominator is always positive
        final BigInteger realDen = denominator.signum() == 1 ? denominator : denominator.negate();

        return new Rational(realNum, realDen, approximate);
    }

    // For testing purpose only!
    @NotNull
    static Rational approximateOf(long numerator, long denominator) {
        return of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), true);
    }

    /**
     * Returns the canonical form of this Rational.
     *
     * <p>The canonical form is an unique way to represent this Rational as an irreductible fraction {@code a/b} where
     * {@code a} and {@code b} are coprime integers and {@code b > 0}.
     *
     * <p><b>Performance notice:</b> This operation implies to compute the GCD of {@code a} and {@code b}, which may be
     * long for high magnitudes. The higher is the magnitude, the longest is this computation. This may be an issue with
     * very high magnitude Rationals, as the one produced by calculus chains or loops. Consider to force canonical form
     * earlier in the process, or to use approximations.
     *
     * @see #magnitude()
     * @see #approximate()
     * @see BigInteger#gcd(BigInteger)
     * @return the canonical form of this Rational, or {@code this} if {@code this} is alread the canonical form.
     */
    public @NotNull Rational canonicalForm() {
        final BigInteger gcd = numerator.gcd(denominator);
        return BigInteger.ONE.equals(gcd) ? this : of(numerator.divide(gcd), denominator.divide(gcd), approximate);
    }

    /**
     * Builds a rational number from two non-null {@code BigInteger} values.
     *
     * @param numerator   the fraction’s numerator.
     * @param denominator the fraction’s denominator.
     * @return the rational number expressed as the quotient of numerator / denominator, normalized: fraction has been
     * reduced if possible (real representation is irreducible), and sign worn by numerator (real denominator is &gt; 0).
     * @throws ArithmeticException if denominator is zero.
     */
    public static @NotNull Rational of(final @NotNull BigInteger numerator, final @NotNull BigInteger denominator) {
        return of(numerator, denominator, false);
    }

    /**
     * Builds a rational number from two {@code long} values.
     *
     * @param numerator   the fraction’s numerator.
     * @param denominator the fraction’s denominator.
     * @return the rational number expressed as the quotient of numerator / denominator, normalized: fraction has been
     * reduced if possible (real representation is irreducible), and sign worn by numerator (real denominator is &gt; 0).
     * @throws ArithmeticException if denominator is zero.
     */
    public static @NotNull Rational of(final long numerator, final long denominator) {
        return of(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    /**
     * Builds an integer "rational" number from a non-null {@code BigInteger} value.
     *
     * @param integer the value to express as a rational.
     * @return the rational number expressed as the provided value / 1.
     */
    public static @NotNull Rational of(final @NotNull BigInteger integer) {
        return of(integer, BigInteger.ONE, false);
    }

    /**
     * Builds an integer "rational" number from a {@code long} value.
     *
     * @param integer the value to express as a rational.
     * @return the rational number expressed as the provided value / 1.
     */
    public static @NotNull Rational of(final long integer) {
        return of(BigInteger.valueOf(integer), BigInteger.ONE, false);
    }

    /**
     * Builds a rational number from a {@code BigDecimal} value.
     *
     * <p>The returned value will be exact but the "scale" notion of {@code BigDecimal} will be lost.
     *
     * <p>With provided {@code decimal}, the rational will be built as
     * {@code decimal.unscaledValue() / 10^(decimal.scale())}
     * Then normalized as every rational build. This implies lost of "scale" notion, and the resulting denominator may
     * not be a power of 10.
     *
     * <p>Examples:
     * <ul>
     *     <li>{@code Rational.of(new BigDecimal("1.23")} gives {@code 123/100}</li>
     *     <li>{@code Rational.of(new BigDecimal("0.700")} gives {@code 7/10} (as well as {@code Rational.of(new BigDecimal("0.70000")})</li>
     *     <li>{@code Rational.of(new BigDecimal("1.50")} gives {@code 3/2} (and not {@code 15/10} which is reducible)</li>
     * </ul>
     *
     * @param decimal the value to express as a rational.
     * @return the value as rational number, normalized: fraction has been reduced if possible (real representation is
     * irreducible), and sign worn by numerator (real denominator is &gt; 0).
     */
    public static @NotNull Rational of(final @NotNull BigDecimal decimal) {
        // Do not use .equals here as we don’t want to compare scales
        if (BigDecimal.ZERO.compareTo(decimal) == 0) {
            return ZERO;
        }
        if (BigDecimal.ONE.compareTo(decimal) == 0) {
            return ONE;
        }

        final BigInteger numerator = decimal.unscaledValue();
        final BigInteger denominator = BigInteger.TEN.pow(decimal.scale());
        return of(numerator, denominator, false);
    }

    /**
     * Shortcut for {@code of(new BigDecimal(s))}
     *
     * @param s the decimal value as {@code String} in base 10
     * @return the value as rational number, normalized: fraction has been reduced if possible (real representation is
     * irreducible), and sign worn by numerator (real denominator is &gt; 0).
     */
    public static @NotNull Rational of(final @NotNull String s) {
        return of(new BigDecimal(s));
    }

    /**
     * Shortcut for {@code of(new BigInteger(numerator), new BigInteger(denominator))}
     *
     * @param numerator   the numerator value as {@code String} in base 10
     * @param denominator the denominator value as {@code String} in base 10
     * @return the rational number expressed as the quotient of numerator / denominator, normalized: fraction has been
     * reduced if possible (real representation is irreducible), and sign worn by numerator (real denominator is &gt; 0).
     * @throws ArithmeticException if denominator is zero.
     */
    public static @NotNull Rational of(final @NotNull String numerator, final @NotNull String denominator) {
        return of(new BigInteger(numerator), new BigInteger(denominator));
    }

    /**
     * <b>This builder is provided to simplify some kind of interfaces and should not be used only unless you know
     * exactly what you are doing!</b>
     *
     * <p>This builder will convert the <b>exact</b> real value from the floating, <i>including all approximations</i>
     * inherent to the IEEE-754 norm. This implies many values with a simple decimal expression that can’t have an exact
     * floating representation, including numbers as common as {@code 0.1}.
     *
     * <p>Examples:
     * <ul>
     *     <li>{@code Rational.of(0.1d)} gives {@code 3602879701896397/36028797018963968}, which is the exact
     *     irreducible value of 0.1 expressed as {@code double} in Java.</li>
     *     <li>{@code Rational.of(0.1f)} gives {@code 13421773/134217728}, which is the exact irreducible value of 0.1
     *     expressed as {@code float} in Java.</li>
     * </ul>
     *
     * <p>Technically, this method checks if the {@code double} is a real finite number, and returns
     * {@code Rational.of(new BigDecimal(number)}.
     *
     * @param d the value to express as a rational.
     * @return the {@code Rational} expressed by the provided double.
     * @throws ArithmeticException if the provided double is not a number or infinite.
     * @see BigDecimal#BigDecimal(double) for details of number conversion.
     */
    public static @NotNull Rational of(final double d) {
        if (Double.isInfinite(d)) {
            throw new ArithmeticException("A rational can’t be infinite.");
        }
        if (Double.isNaN(d)) {
            throw new ArithmeticException("NaN can’t be converted as rational.");
        }
        return of(new BigDecimal(d));
    }
    // endregion

    // region Number manipulation

    /**
     * Negates the current rational.
     *
     * @return The negation of the current rational, or zero if this is zero. Numerator is the negation of this
     * numerator, denominator remains untouched.
     */
    public @NotNull Rational negate() {
        if (this == ZERO || this == APPROX_ZERO) {
            return this;
        }
        return Rational.of(numerator.negate(), denominator, approximate);
    }

    /**
     * Inverts the current rational.
     *
     * @return The inverse of the current rational (numerator is the current denominator, denominator is the current
     * numerator, result is normalized).
     * @throws ArithmeticException if this is zero.
     */
    public @NotNull Rational inverse() {
        if (this == ZERO || this == APPROX_ZERO) {
            throw new ArithmeticException("Can’t inverse zero.");
        }
        return Rational.of(denominator, numerator, approximate);
    }

    /**
     * Return the absolute value of the current rational.
     *
     * @return the absolute value of the current rational, or zero if this is zero.
     */
    public @NotNull Rational abs() {
        if (this == ZERO || this == APPROX_ZERO || (numerator.signum() > 0)) {
            return this;
        }
        return Rational.of(numerator.negate(), denominator, approximate);
    }
    // endregion

    // region Basic arithmetic
    private static @Nullable Rational identityOperation(
            final @NotNull Rational a,
            final @NotNull Rational b,
            final @NotNull Rational identityValue,
            final @NotNull Rational approxIdentityValue) {
        if (a.equals(identityValue)) {
            return b;
        }
        if (a.equals(approxIdentityValue)) {
            return b.approximate ? b : Rational.of(b.numerator, b.denominator, true);
        }
        if (b.equals(identityValue)) {
            return a;
        }
        if (b.equals(approxIdentityValue)) {
            return a.approximate ? a : Rational.of(a.numerator, a.denominator, true);
        }
        return null;
    }

    /**
     * Returns a Rational whose value is {@code (this + val)}
     *
     * @param val value to be added to this Rational
     * @return {@code (this + val)}
     */
    public @NotNull Rational add(final @NotNull Rational val) {
        final Rational identityResult = identityOperation(this, val, ZERO, APPROX_ZERO);
        return identityResult == null
                ? Rational.of(
                        (numerator.multiply(val.denominator)).add(denominator.multiply(val.numerator)),
                        denominator.multiply(val.denominator),
                        approximate || val.approximate)
                : identityResult;
    }

    /**
     * Returns the sum of all Rational in the collection.
     *
     * @param vals a collection of rationals to sum.
     * @return The sum of all Rational in the collection, or 0 if the collection is empty.
     */
    public static @NotNull Rational sum(final @NotNull Collection<Rational> vals) {
        Rational sum = Rational.ZERO;
        for (Rational rational : vals) {
            sum = sum.add(rational);
        }
        return sum;
    }

    /**
     * Returns the sum of all Rational in the array.
     *
     * @param vals an array of rationals to sum.
     * @return The sum of all Rational in the array, or 0 if the collection is empty.
     */
    public static @NotNull Rational sum(final @NotNull Rational... vals) {
        return sum(Arrays.asList(vals));
    }

    /**
     * Adds all the rationals in the collection to the current one.
     *
     * @param vals a collection of rationals
     * @return the sum of this rational and all the rational provided.
     */
    public @NotNull Rational addAll(final @NotNull Collection<Rational> vals) {
        return this.add(sum(vals));
    }

    /**
     * Adds all the rationals in the collection to the current one.
     *
     * @param vals a collection of rationals
     * @return the sum of this rational and all the rational provided.
     */
    public @NotNull Rational addAll(final @NotNull Rational... vals) {
        return this.add(sum(vals));
    }

    public @NotNull Rational subtract(final @NotNull Rational val) {
        final Rational identityResult = identityOperation(this, val, ZERO, APPROX_ZERO);
        return identityResult == null
                ? Rational.of(
                        (numerator.multiply(val.denominator)).subtract(denominator.multiply(val.numerator)),
                        denominator.multiply(val.denominator),
                        approximate || val.approximate)
                : identityResult;
    }

    /**
     * Returns a rational whose value is {@code this * val}
     * @param val the value to be multiplied by this Rational
     * @return {@code this * val}
     */
    public @NotNull Rational multiply(final @NotNull Rational val) {
        if (this == ZERO || val == ZERO) {
            return ZERO;
        }
        if (this == APPROX_ZERO || val == APPROX_ZERO) {
            return APPROX_ZERO;
        }
        final Rational identityResult = identityOperation(this, val, ONE, APPROX_ONE);
        return identityResult == null
                ? Rational.of(
                        numerator.multiply(val.numerator),
                        denominator.multiply(val.denominator),
                        approximate || val.approximate)
                : identityResult;
    }

    public static @NotNull Rational product(final @NotNull Collection<Rational> vals) {
        Rational product = Rational.ONE;
        for (Rational val : vals) {
            product = product.multiply(val);
        }
        return product;
    }

    public static @NotNull Rational product(final @NotNull Rational... vals) {
        return product(Arrays.asList(vals));
    }

    public @NotNull Rational multiplyAll(final @NotNull Collection<Rational> vals) {
        return this.multiply(product(vals));
    }

    public @NotNull Rational multiplyAll(final @NotNull Rational... vals) {
        return this.multiply(product(vals));
    }

    public @NotNull Rational divide(final @NotNull Rational val) {
        if (val == ZERO || val == APPROX_ZERO) {
            throw new ArithmeticException("Division by 0.");
        }
        if (this == ZERO || this == APPROX_ZERO) {
            return this;
        }
        final Rational identityResult = identityOperation(this, val, ONE, APPROX_ONE);
        return identityResult == null
                ? Rational.of(
                        numerator.multiply(val.denominator),
                        denominator.multiply(val.numerator),
                        approximate || val.approximate)
                : identityResult;
    }

    public @NotNull Rational pow(final int exponent) {
        // (a / b)^0 = 1 (always, including 0^0)
        if (exponent == 0) {
            return ONE;
        }
        if (exponent == 1 || this == ONE || this == APPROX_ONE) {
            return this;
        }
        // (a / b)^p = a^p / b^p
        // (a / b)^-p = b^p / a^p
        return exponent > 0
                ? Rational.of(numerator.pow(exponent), denominator.pow(exponent), approximate)
                : Rational.of(denominator.pow(-exponent), numerator.pow(-exponent), approximate);
    }
    // endregion

    // region Getters and metadata
    public boolean isApproximate() {
        return approximate;
    }

    public boolean isInteger() {
        return BigInteger.ONE.equals(denominator)
                || numerator.remainder(denominator).equals(BigInteger.ZERO);
    }

    @Contract(pure = true)
    @Range(from = -1, to = 1)
    public int signum() {
        return numerator.signum();
    }

    @NotNull
    BigInteger getNumerator() {
        return numerator;
    }

    @NotNull
    BigInteger getDenominator() {
        return denominator;
    }
    // endregion

    // region Comparison

    /**
     * Compares this {@code Rational} with the specified {@code Rational}. Two {@code Rational} objects that are equals
     * in value but have different pairs of numerators and denominators (example: {@code 2/4} and {@code 3/6}) are
     * equals. The approximate flag is ignored, so an approximate {@code Rational} will be considered equal to any other
     * {@code Rational} which is equal in value, despite this other {@code Rational} is approximate or not. This is
     * different from the {@link #equals(Object)} behaviour.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return -1, 0 or 1 as this {@code Rational} is numerically less than, equal to, or greater than {@code val}.
     */
    @Override
    public int compareTo(final @NotNull Rational val) {
        // a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
        // specified object.
        if (val == this) {
            return 0;
        }

        // Not the same numerator sign -> direct comparison (denominator is always > 0)
        if (numerator.signum() != val.numerator.signum()) {
            return Integer.compare(numerator.signum(), val.numerator.signum());
        }

        // Same denominator -> direct numerator comparison
        if (denominator.compareTo(val.denominator) == 0) {
            return numerator.compareTo(val.numerator);
        }

        // (a/b) == (c/d) if and only if a*d == b*c
        // Also, if both denominators are positive (which is the case here):
        // (a/b) == (c/d) if and only if a*d < b*c
        return numerator.multiply(val.denominator).compareTo(denominator.multiply(val.numerator));
    }

    /**
     * Compares this {@code Rational} with the specified {@code Object} for equality.
     *
     * <p>Unlike {@link #compareTo(Rational)}, an approximate {@code Rational} is <b>never</b> equals to another
     * {@code Rational}, except himself (the same object).
     *
     * @param o {@code Object} to which this {@code Rational} is to be compared.
     * @return {@code true} if and only if the specified {@code Object} is a {@code Rational} who is not approximate.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        // An approximate rational is never equal to another rational (except itself – the same object)
        Rational val = (Rational) o;
        if (approximate || val.approximate) {
            return false;
        }
        // (a/b) == (c/d) if and only if a*d == b*c
        return numerator.multiply(val.denominator).equals(denominator.multiply(val.numerator));
    }

    @Override
    public int hashCode() {
        // Trick to comply hashCode()/equals() general contract
        if (this.equals(ZERO)) {
            return 0;
        }
        return numerator.abs().compareTo(denominator) > 0
                // |numerator| > denominator : use intValue
                ? intValue()
                // |numerator| < denominator : intValue is always 0 -> use intValue of inverse
                : (denominator.abs().divide(numerator)).intValue();
    }
    // endregion

    // region Comparison (helpers)

    /**
     * Shortcut for {@code compareTo(val) > 0}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code true} if this {@code Rational} is numerically greater than {@code val}, {@code false} otherwise.
     */
    public boolean gt(final @NotNull Rational val) {
        return compareTo(val) > 0;
    }

    /**
     * Shortcut for {@code compareTo(val) >= 0}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code true} if this {@code Rational} is numerically greater or equal than {@code val}, {@code false}
     * otherwise.
     */
    public boolean ge(final @NotNull Rational val) {
        return compareTo(val) >= 0;
    }

    /**
     * Shortcut for {@code compareTo(val) < 0}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code true} if this {@code Rational} is numerically less than {@code val}, {@code false} otherwise.
     */
    public boolean lt(final @NotNull Rational val) {
        return compareTo(val) < 0;
    }

    /**
     * Shortcut for {@code compareTo(val) <= 0}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code true} if this {@code Rational} is numerically less or equal than {@code val}, {@code false}
     * otherwise.
     */
    public boolean le(final @NotNull Rational val) {
        return compareTo(val) <= 0;
    }
    // endregion

    // region Statistics

    /**
     * Select the smallest {@code Rational} between this {@code Rational} and the specified {@code Rational}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code this} if {@code this.compareTo(val) <= 0}, {@code val} elsewhere.
     */
    public @NotNull Rational min(final @NotNull Rational val) {
        return compareTo(val) <= 0 ? this : val;
    }

    /**
     * Select the largest {@code Rational} between this {@code Rational} and the specified {@code Rational}.
     *
     * @param val {@code Rational} to which this {@code Rational} is to be compared.
     * @return {@code this} if {@code this.compareTo(val) >= 0}, {@code val} elsewhere.
     */
    public @NotNull Rational max(final @NotNull Rational val) {
        return compareTo(val) >= 0 ? this : val;
    }

    /**
     * The smallest {@code Rational} in the {@code Collection} provided.
     *
     * @implNote If there are multiple minimum {@code Rational} with the same value in the collection, the selected one
     * can be any of these.
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s {@code Collection} to find the smallest value.
     * @return The smallest {@code Rational} in the {@code Collection} provided.
     */
    public static @NotNull Rational min(final @NotNull Collection<Rational> vals) {
        if (vals.size() == 0) {
            throw new ArithmeticException("Cannot compute the minimum of empty set.");
        }
        return vals.stream().min(Comparator.naturalOrder()).get();
    }

    /**
     * The smallest {@code Rational} in the array provided.
     *
     * @implNote If there are multiple minimum {@code Rational} with the same value in the array, the selected one can
     * be any of these.
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s array to find the smallest value.
     * @return The smallest {@code Rational} in the array provided.
     */
    public static @NotNull Rational min(final @NotNull Rational... vals) {
        return min(Arrays.asList(vals));
    }

    /**
     * The largest {@code Rational} in the {@code Collection} provided.
     *
     * @implNote If there are multiple maximum {@code Rational} with the same value in the collection, the selected one
     * can be any of these.
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s {@code Collection} to find the largest value.
     * @return The largest {@code Rational} in the {@code Collection} provided.
     */
    public static @NotNull Rational max(final @NotNull Collection<Rational> vals) {
        if (vals.size() == 0) {
            throw new ArithmeticException("Cannot compute the maximum of empty set.");
        }
        return vals.stream().max(Comparator.naturalOrder()).get();
    }

    /**
     * The largest {@code Rational} in the array provided.
     *
     * @implNote If there are maximum minimum {@code Rational} with the same value in the array, the selected one can
     * be any of these.
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s array to find the largest value.
     * @return The largest {@code Rational} in the array provided.
     */
    public static @NotNull Rational max(final @NotNull Rational... vals) {
        return max(Arrays.asList(vals));
    }

    /**
     * Computes the average value of the {@code Rational}’s {@code Collection} provided.
     *
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s {@code Collection} to compute the average value.
     * @return {@code sum(all vals) / count(vals)}
     */
    public static @NotNull Rational average(final @NotNull Collection<Rational> vals) {
        if (vals.size() == 0) {
            throw new ArithmeticException("Cannot compute the average of empty set.");
        }
        return sum(vals).divide(Rational.of(vals.size()));
    }

    /**
     * Compute the median value of the  {@code Rational}’s {@code Collection} provided.
     *
     * @throws ArithmeticException when {@code vals} is empty
     * @param vals a {@code Rational}’s {@code Collection} to compute the average value.
     * @return the median value of the provided collection.
     */
    public static @NotNull Rational median(final @NotNull Collection<Rational> vals) {
        if (vals.size() == 0) {
            throw new ArithmeticException("Cannot compute the median of empty set.");
        }
        final List<Rational> sorted = new ArrayList<>(vals);
        Collections.sort(sorted);
        final int middleIndex = vals.size() / 2;
        return vals.size() % 2 == 0
                ? sorted.get(middleIndex - 1).add(sorted.get(middleIndex)).divide(Rational.of(2))
                : sorted.get(middleIndex);
    }
    // endregion

    // region "Number" extension methods (and similar)
    /**
     * Returns the value of the specified number as a {@code BigInteger}.
     *
     * @implNote This is a "fast" implementation that returns {@code numerator // denominator} where {@code //} is the
     * integer division. For a more precise conversion, call {@code approximate(1)} before.
     * @return the numeric value represented by this object after conversion to type {@code BigInteger}
     */
    public @NotNull BigInteger bigIntegerValue() {
        return numerator.divide(denominator);
    }

    /**
     * Returns the value of the specified number as a {@code BigDecimal}.
     *
     * @implNote The output {@code BigDecimal} has an enormous scale of 1074, which is required to avoid precision lost
     * on further convertion to {@code double} on worst cases.
     * @return the numeric value represented by this object after conversion to type {@code BigInteger}
     */
    public @NotNull BigDecimal bigDecimalValue() {
        return new BigDecimal(numerator, DOUBLE_REQUIRED_SCALE)
                .divide(new BigDecimal(denominator, DOUBLE_REQUIRED_SCALE), RoundingMode.HALF_EVEN);
    }

    @Override
    public int intValue() {
        return bigIntegerValue().intValue();
    }

    @Override
    public long longValue() {
        return bigIntegerValue().longValue();
    }

    @Override
    public float floatValue() {
        if (signum() == 0) {
            return 0.0f;
        }
        return bigDecimalValue().floatValue();
    }

    @Override
    public double doubleValue() {
        if (signum() == 0) {
            return 0.0d;
        }
        return bigDecimalValue().doubleValue();
    }
    // endregion

    // region Tooling

    /**
     * Returns the number of bits in the minimal representation of this Rational, <em>excluding</em> a sign bit.
     * (Computes {@code (ceil(log2(numerator < 0 ? -numerator : numerator+1)) + ceil(log2(denominator + 1)}, as
     * denominator is always &gt; 1).
     *
     * <p>This is useful to detect potential performance issues.
     *
     * <p>The implementation uses {@code BigInteger#bitLength()} on both numerator and denominator. This leads to some
     * strange boundary behaviours, as {@code (0).bitLength() = 0}, {@code (1).bitLength() = 1} but
     * {@code (-1).bitLength() = 0}. This is not a problem as this methods returns the magnitude of the Rational and not
     * an exact metric.
     *
     * @see BigInteger#bitLength()
     * @return  the number of bits in the minimal representation of this Rational, <em>excluding</em> a sign bit.
     */
    public int magnitude() {
        return numerator.bitLength() + denominator.bitLength();
    }

    /**
     * Returns an approximation of this Rational with the denominator is {@code <= 2^128}. Return {@code this} if the
     * condition is already matched. If an approximation is done, the returned rational is flagged as approximate.
     *
     * <p>This is useful on long chains of calculus that leads to unreasonable long numerators and denominators and
     * canonical forms doesn’t help.
     *
     * @return an approximation of this Rational with the denominator is {@code <= 2^128}.
     */
    public @NotNull Rational approximate() {
        return approximate(BigInteger.TWO.pow(128));
    }

    /**
     * Returns an approximation of this Rational with the denominator is {@code <= denominatorMax}. Return {@code this}
     * if the condition is already matched. If an approximation is done, the returned rational is flagged as approximate.
     *
     * <p>This is useful on long chains of calculus that leads to unreasonable long numerators and denominators and
     * canonical forms doesn’t help.
     *
     * @param denominatorMax The maximum value for the denominator, included.
     * @return an approximation of this Rational with the denominator is {@code <= denominatorMax}.
     */
    public @NotNull Rational approximate(final long denominatorMax) {
        return approximate(BigInteger.valueOf(denominatorMax));
    }

    /**
     * Returns an approximation of this Rational with the denominator is {@code <= denominatorMax}. Return {@code this}
     * if the condition is already matched. If an approximation is done, the returned rational is flagged as approximate.
     *
     * <p>This is useful on long chains of calculus that leads to unreasonable long numerators and denominators and
     * canonical forms doesn’t help.
     *
     * @implNote current implementation will return {@code this} if current denominator is smaller or equal than, and
     * an approximation of current Rational whose denominator is exactly the one in parameter elsewhere. The result may
     * not be a canonical form.
     *
     * @throws ArithmeticException if the {@code denominatorMax} is negative or zero.
     * @param denominatorMax The maximum value for the denominator, included.
     * @return an approximation of this Rational with the denominator is {@code <= denominatorMax}.
     */
    public @NotNull Rational approximate(final @NotNull BigInteger denominatorMax) {
        if (denominatorMax.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("Target maximum denominator must be > 0.");
        }
        if (denominator.compareTo(denominatorMax) <= 0) {
            return this;
        }
        // There should be a more efficient way…
        final BigInteger ratio = denominator.divide(denominatorMax);
        final BigInteger newNumerator = numerator.divide(ratio);
        final Rational approxFloor = Rational.of(newNumerator, denominatorMax, true);
        final Rational epsilonFloor = this.subtract(approxFloor).abs();
        if (Rational.ZERO.compareTo(epsilonFloor) == 0) {
            // Exact value.
            return Rational.of(newNumerator, denominatorMax);
        }

        final Rational approxCeil = Rational.of(newNumerator.add(BigInteger.ONE), denominatorMax, true);
        final Rational epsilonCeil = this.subtract(approxCeil).abs();

        return epsilonFloor.le(epsilonCeil) ? approxFloor : approxCeil;
    }

    @Override
    public String toString() {
        String out = stringCache;
        if (out == null) {
            out = stringCache = (approximate ? "~" : "")
                    + numerator
                    + (BigInteger.ONE.equals(denominator) ? "" : ("/" + denominator));
        }
        return out;
    }
    // endregion
}
