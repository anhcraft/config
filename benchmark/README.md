# Benchmark

## Table of contents
- DirectGsonBenchmark, PreparedGsonBenchmark, NormalizerGsonBenchmark
- AdapterProviderBenchmark

## Information
```
# JMH version: 1.37
# VM version: JDK 11.0.19, OpenJDK 64-Bit Server VM, 11.0.19+7
# VM invoker: adoptium-11-x64-hotspot-windows
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 3 iterations, 1 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
```

## DirectGsonBenchmark
- Given: POJO
- Measure: GSON serialization time

```
Benchmark                                     Mode  Cnt     Score      Error   Units
DirectGsonBenchmark.benchmarkConfigGson       avgt    5   6981,339 ±  125,938  ns/op
```

## PreparedGsonBenchmark
- Given: Normalized POJO as a Dictionary
- Measure: GSON serialization time

```
Benchmark                                     Mode  Cnt     Score      Error   Units
PreparedGsonBenchmark.benchmarkConfigGson     avgt    5   7513,901 ±  293,557  ns/op
```

## NormalizerGsonBenchmark
- Given: POJO
- Measure: Normalization + GSON serialization time

```
Benchmark                                    Mode  Cnt      Score      Error   Units
NormalizerGsonBenchmark.benchmarkConfigGson   avgt    5  13854,957 ± 1711,302  ns/op
```

## AdapterProviderBenchmark
- Given: POJO
- Measure: Normalization time using 3 different type adapters

```
Benchmark                                     Mode  Cnt     Score      Error  Units
AdapterProviderBenchmark.useCacheableAdapter  avgt    5  4758,886 ±  554,990  ns/op
AdapterProviderBenchmark.useIndexedAdapter    avgt    5  5781,609 ± 1286,541  ns/op
AdapterProviderBenchmark.useSimpleAdapter     avgt    5  5566,992 ±  385,030  ns/op
```
