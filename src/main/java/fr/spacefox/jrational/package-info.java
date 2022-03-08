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
 * See {@link java.lang.Comparable}, {@link java.util.SortedSet} or {@link java.util.SortedMap} for more information.
 *
 * @see java.math.BigInteger
 * @see java.util.SortedMap
 * @see java.util.SortedSet
 * @author SpaceFox
 */
package fr.spacefox.jrational;
