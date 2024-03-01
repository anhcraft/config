package execution;

import com.google.gson.Gson;
import common.Store;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, iterations = 3)
@Fork(1)
public class DirectGsonBenchmark {
  private Gson gson;
  private Store model;

  @Setup
  public void setup() {
    gson = new Gson();
    model = Store.createDummy();
  }

  //@Benchmark
  public void benchmarkConfigGson(Blackhole hell) {
    hell.consume(gson.toJson(model));
  }
}
