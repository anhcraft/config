package execution;

import common.Store;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.ConfigNormalizer;
import dev.anhcraft.config.adapter.CacheableAdapterProvider;
import dev.anhcraft.config.adapter.IndexedAdapterProvider;
import dev.anhcraft.config.adapter.SimpleAdapterProvider;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, iterations = 3)
@Fork(1)
public class AdapterProviderBenchmark {
  private Store model;
  private ConfigNormalizer normalizerUsingSimpleAdapter;
  private ConfigNormalizer normalizerUsingIndexedAdapter;
  private ConfigNormalizer normalizerUsingCacheableAdapter;

  @Setup
  public void setup() {
    model = Store.createDummy();
    normalizerUsingSimpleAdapter = ConfigFactory.create().useAdapterProvider(SimpleAdapterProvider.class).build().getNormalizer();
    normalizerUsingIndexedAdapter = ConfigFactory.create().useAdapterProvider(IndexedAdapterProvider.class).build().getNormalizer();
    normalizerUsingCacheableAdapter = ConfigFactory.create().useAdapterProvider(CacheableAdapterProvider.class).build().getNormalizer();
  }

  @Benchmark
  public void useSimpleAdapter(Blackhole hell) throws Exception {
    hell.consume(normalizerUsingSimpleAdapter.normalize(model));
  }

  @Benchmark
  public void useIndexedAdapter(Blackhole hell) throws Exception {
    hell.consume(normalizerUsingIndexedAdapter.normalize(model));
  }

  @Benchmark
  public void useCacheableAdapter(Blackhole hell) throws Exception {
    hell.consume(normalizerUsingCacheableAdapter.normalize(model));
  }
}
