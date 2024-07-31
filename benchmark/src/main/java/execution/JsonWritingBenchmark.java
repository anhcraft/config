package execution;

import com.google.gson.Gson;
import common.Store;
import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.ConfigNormalizer;
import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.json.JsonWriter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, iterations = 3)
@Measurement(time = 1, iterations = 2)
@Fork(1)
public class JsonWritingBenchmark {
  private ConfigNormalizer normalizer;
  private Store model;
  private Gson gson;
  private Dictionary dictionary;

  @Setup
  public void setup() throws Exception {
    model = Store.createDummy();
    gson = new Gson();
    normalizer = ConfigFactory.create().build().getNormalizer();
    dictionary = (Dictionary) normalizer.normalize(model);
  }

  @Benchmark
  public void writingJsonFromModelUsingGson(Blackhole hell) {
    hell.consume(gson.toJson(model));
  }

  @Benchmark
  public void writingJsonFromModelUsingCustom(Blackhole hell) throws Exception {
    StringWriter sw = new StringWriter();
    JsonWriter jw = new JsonWriter(new BufferedWriter(sw));
    jw.serialize(normalizer.normalize(model));
    hell.consume(sw.toString());
  }

  @Benchmark
  public void writingJsonFromDictionaryUsingGson(Blackhole hell) {
    hell.consume(gson.toJson(dictionary));
  }

  @Benchmark
  public void writingJsonFromDictionaryUsingCustom(Blackhole hell) throws IOException {
    StringWriter sw = new StringWriter();
    JsonWriter jw = new JsonWriter(new BufferedWriter(sw));
    jw.serialize(dictionary);
    hell.consume(sw.toString());
  }
}
