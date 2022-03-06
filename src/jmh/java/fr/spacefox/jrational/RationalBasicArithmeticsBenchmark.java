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
public class RationalBasicArithmeticsBenchmark {

    private static final Random RANDOM = new Random();

    private Rational a;
    private Rational b;
    private int pow;

    //    @Param({"10", "100", "1000", "10000"})
    @Param({"2", "4", "8", "16", "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192", "16384"})
    int size;

    @Setup(Level.Iteration)
    public void setup() {
        a = Rational.of(stringNumberOfLength(size / 2), stringNumberOfLength(size / 2));
        b = Rational.of(stringNumberOfLength(size / 2), stringNumberOfLength(size / 2));
        pow = RANDOM.nextInt(100) + 1;
    }

    private static String stringNumberOfLength(int length) {
        final String out = IntStream.range(0, length)
                .mapToObj(i -> String.valueOf(RANDOM.nextInt(10)))
                .collect(Collectors.joining());
        return out.equals("0") ? stringNumberOfLength(length) : out;
    }

    @Benchmark
    public Rational add() {
        return a.add(b);
    }

    @Benchmark
    public Rational subtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Rational multiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Rational divide() {
        return a.divide(b);
    }

    @Benchmark
    public Rational pow() {
        return a.pow(pow);
    }
}
