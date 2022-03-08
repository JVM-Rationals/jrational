# JRational

## Presentation

Immutable rational numbers. Quotient `p/q` of two integers, a numerator p and a non-zero denominator q.

Internal representation uses two `BigInteger` for numerator and denominator. This has the following implications:
1. Precision: `Rational`’s precision depends on `BigInteger` precision, which is arbitrary and virtually infinite.
2. Performances: `Rational`’s performance highly depends on `BigInteger` performances, as most operation are backed by 
   computations on `BigInteger`.

Semantics of arithmetics operation and public API method names also follows `BigInteger` ones. Also, an 
`ArithmeticException` is thrown when a builder or a method would attempt an illegal operation.

**Constructors and builders:** There is no public constructor for `Rational`. The only way to create a `Rational` is to
use a static builder `Rational.of(something)`.

**Limitations:** There is only one zero (no positive or negative zero), and special values like infinities and"not a 
number" cannot be represented with a `Rational`.

**Precision and performances:** As performances directly depends on underlying BigInteger arithmetics. This implies the
more "precise" the `Rational` is, slower it is. Preliminary tests seems the computing complexity is in O(n), with n the 
size of numerator and denominator. This is important as chained calculus may lead to an irreducible rational which still
is a quotient of two very large integers (thousands of decimal figures) then slow to handle, even if the rational number 
itself isn’t very large or small. The method `magnitude()` method will help to detect `Rational` backed with large 
`BigInteger`; and the `approximate()` and `canonicalForm()` methods allows to shrink them to more reasonable numbers.

**Approximate rationals:** A `Rational` may be approximate (see `isApproximate()` method). This denotes this rational is
only an approximation of the real value. The real value may be, or not, an irrational number in the mathematical
definition. There is no constructor nor builder to create an approximate Rational, as all the builder require exact 
values. There are three ways to get an approximate `Rational`. First is provided constants. Second is to call 
`approximate()` on an existing number, the result will be flagged as approximate if and only if an approximation has
been done. The second one is through arithmetic methods that can lead to irrational results. Any operation that implies
an approximate Rational will always have an approximate as result.

## Technical data

This project requires Java 11+.

It doesn’t have any runtime dependency except the `java.base` module.

# How to use it in my project?

This library will be available [on Maven Central / Sonatype](https://search.maven.org/artifact/fr.spacefox/jrational)
and [on MVN Repository](https://mvnrepository.com/artifact/fr.spacefox/jrational).

Import declarations:

Maven:
```xml
<dependency>
  <groupId>fr.spacefox</groupId>
  <artifactId>jrational</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Gradle (Groovy DSL):
```groovy
implementation 'fr.spacefox:jrational:1.0.0-SNAPSHOT'
```

Gradle (Kotlin DSL):
```kotlin
implementation("fr.spacefox:jrational:1.0.0-SNAPSHOT")
```

# Documentation

## Public API

[See Javadoc for details](https://jvm-rationals.github.io/jrational/).

## JRational versions

JRational follows [semver](https://semver.org).

## What’s next?

### Before release v1.0.0

- [ ] Rational exponentiation (including roots, which is just exponentiation with `1/n` exponent).

Expected soon™.

### Futures versions

- [ ] Trigonometric functions
- [ ] Logarithm and exponentiation

Expected when I have time and motivation.

# Contributions welcome!

Contributions are open. Feel free to open tickets and merge requests.

This projects uses Gradle 7.4.