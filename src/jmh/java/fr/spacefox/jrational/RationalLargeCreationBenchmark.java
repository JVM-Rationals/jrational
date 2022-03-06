package fr.spacefox.jrational;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RationalLargeCreationBenchmark {
    private static final Random RANDOM = new Random();

    private String equalsLengthNum;
    private String equalsLengthDen;
    private String longNumber;

    //    @Param({"10", "100", "1000", "10000"})
    @Param({"2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192", "16384"})
    int size;

    @Setup(Level.Iteration)
    public void setup() {
        longNumber = stringNumberOfLength(size - 1);
        equalsLengthNum = stringNumberOfLength(size / 2);
        equalsLengthDen = stringNumberOfLength(size / 2);
    }

    private static String stringNumberOfLength(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(RANDOM.nextInt(10)))
                .collect(Collectors.joining());
    }

    // These benchmarks checks the creation time of a Rational from String of different sizes.
    // The total number of figures in (numerator + denominator) is always "size".
    @Benchmark
    public Rational newRationalIntegerFromLongString() {
        return Rational.of(longNumber);
    }

    @Benchmark
    public Rational newRationalInverseFromLongString() {
        return Rational.of("1", longNumber);
    }

    @Benchmark
    public Rational newRationalFromTwoLongStrings() {
        return Rational.of(equalsLengthNum, equalsLengthDen);
    }

    @Benchmark
    public Rational newCanonicalRationalIntegerFromLongString() {
        return Rational.of(longNumber).canonicalForm();
    }

    @Benchmark
    public Rational newCanonicalRationalInverseFromLongString() {
        return Rational.of("1", longNumber).canonicalForm();
    }

    @Benchmark
    public Rational newCanonicalRationalFromTwoLongStrings() {
        return Rational.of(equalsLengthNum, equalsLengthDen).canonicalForm();
    }
}
