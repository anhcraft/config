package execution;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import common.Store;
import dev.anhcraft.config.ConfigDenormalizer;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.SettingFlag;
import dev.anhcraft.config.json.JsonParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, iterations = 3)
@Measurement(time = 1, iterations = 5)
@Fork(1)
public class JsonParsingBenchmark {
  private ConfigDenormalizer denormalizer;
  private Gson gson;
  private String json;

  @Setup
  public void setup() throws Exception {
    ConfigFactory fac = ConfigFactory.create()
      .enableDenormalizerSetting(SettingFlag.Denormalizer.DISABLE_VALIDATION)
      .build();
    denormalizer = fac.getDenormalizer();
    gson = new Gson();
    json = gson.toJson(Store.createDummy());
    denormalizer.denormalize(json, Store.class); // trigger cache
  }

  @Benchmark
  public void parsingJsonToModelUsingGson(Blackhole hell) {
    hell.consume(gson.fromJson(json, Store.class));
  }

  @Benchmark
  public void parsingJsonToModelUsingCustom(Blackhole hell) throws Exception {
    StringReader sr = new StringReader(json);
    JsonParser jp = new JsonParser(new BufferedReader(sr));
    hell.consume(denormalizer.denormalize(jp.parse(), Store.class));
  }

  @Benchmark
  public void parsingJsonToDictionaryUsingGson(Blackhole hell) {
    hell.consume(gson.fromJson(json, JsonObject.class));
  }

  @Benchmark
  public void parsingJsonToDictionaryUsingCustom(Blackhole hell) throws IOException {
    StringReader sr = new StringReader(json);
    JsonParser jp = new JsonParser(new BufferedReader(sr));
    hell.consume(jp.parse());
  }
}
