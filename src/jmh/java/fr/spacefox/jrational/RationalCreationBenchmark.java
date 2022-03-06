package fr.spacefox.jrational;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
public class RationalCreationBenchmark {

    private long longNum;
    private long longDen;

    private String strNum;
    private String strDen;

    @Setup(Level.Iteration)
    public void setup() {
        final Random random = new Random();

        longNum = random.nextLong();
        longDen = random.nextLong();
        strNum = Long.toString(longNum);
        strDen = Long.toString(longDen);
    }

    @Benchmark
    public Rational newRationalFromLongs() {
        return Rational.of(longNum, longDen);
    }

    @Benchmark
    public Rational newRationalFromStrings() {
        return Rational.of(strNum, strDen);
    }
}
