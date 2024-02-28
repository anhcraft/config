# Benchmark

## Information
```
# JMH version: 1.37
# VM version: JDK 11.0.19, OpenJDK 64-Bit Server VM, 11.0.19+7
# VM invoker: adoptium-11-x64-hotspot-windows
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 3 iterations, 3 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
```

## DirectGsonBenchmark
- Given: POJO
- Measure: GSON serialization time

```
Benchmark                                Mode  Cnt     Score     Error  Units
DirectGsonBenchmark.benchmarkConfigGson  avgt   25  7826,847 ± 783,365  ns/op
```

## PreparedGsonBenchmark
- Given: Normalized POJO as a Dictionary
- Measure: GSON serialization time

```
Benchmark                                  Mode  Cnt     Score     Error  Units
PreparedGsonBenchmark.benchmarkConfigGson  avgt   25  8312,609 ± 668,471  ns/op
```

## NormalizerGsonBenchmark
- Given: POJO
- Measure: Normalization + GSON serialization time

```
Benchmark                                    Mode  Cnt      Score     Error  Units
NormalizerGsonBenchmark.benchmarkConfigGson  avgt   25  14251,344 ± 663,283  ns/op
```
