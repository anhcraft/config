package execution;

import com.google.gson.Gson;
import common.Store;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.ConfigNormalizer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, iterations = 3)
@Fork(1)
public class NormalizerGsonBenchmark {
  private Gson gson;
  private Store model;
  private ConfigNormalizer normalizer;

  @Setup
  public void setup() {
    gson = new Gson();
    model = Store.createDummy();
    normalizer = ConfigFactory.create().build().getNormalizer();
  }

  //@Benchmark
  public void benchmarkConfigGson(Blackhole hell) throws Exception {
    hell.consume(gson.toJson(normalizer.normalize(model)));
  }
}
