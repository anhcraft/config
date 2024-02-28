package execution;

import com.google.gson.Gson;
import common.Store;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.Dictionary;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 3, iterations = 3)
public class PreparedGsonBenchmark {
  private Gson gson;
  private Dictionary dictionary;

  @Setup
  public void setup() throws Exception {
    gson = new Gson();
    dictionary = (Dictionary) ConfigFactory.create().build().getNormalizer().normalize(Store.createDummy());
  }

  //@Benchmark
  public void benchmarkConfigGson(Blackhole hell) {
    hell.consume(gson.toJson(dictionary));
  }
}
