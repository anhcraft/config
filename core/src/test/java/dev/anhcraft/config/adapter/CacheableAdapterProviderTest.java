package dev.anhcraft.config.adapter;

import static org.junit.jupiter.api.Assertions.*;

import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class CacheableAdapterProviderTest {
  /*

   Hierarchy:
             A
           /  \
          B    C <-- Inf1
         / \    \    \/
  Inf2---D  E     F--<<<--Inf2
   /\        \   / \
  Inf3--------G  H  I

    Discoverable:
             A <-- Object
           /  \
          B    C <-- Inf1
           \    \    \/
            E     F--<<<--Inf2
                 /         /\
                H         Inf3

    */

  @Test
  public void test() throws Exception {
    LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters = new LinkedHashMap<>();
    typeAdapters.put(DummyB.class, new TypeAdapter1());
    typeAdapters.put(Inf2.class, new TypeAdapter2());
    typeAdapters.put(DummyE.class, new TypeAdapter3());
    typeAdapters.put(DummyH.class, new TypeAdapter1());
    AdapterProvider provider = new CacheableAdapterProvider(typeAdapters);
    assertInstanceOf(TypeAdapter1.class, provider.getTypeAdapter(DummyB.class));
    assertInstanceOf(TypeAdapter2.class, provider.getTypeAdapter(Inf2.class));
    assertInstanceOf(TypeAdapter3.class, provider.getTypeAdapter(DummyE.class));
    assertInstanceOf(TypeAdapter1.class, provider.getTypeAdapter(DummyH.class));
    assertNull(provider.getTypeAdapter(Object.class));
    assertNull(provider.getTypeAdapter(DummyA.class));
    assertNull(provider.getTypeAdapter(DummyC.class));
    assertInstanceOf(TypeAdapter2.class, provider.getTypeAdapter(DummyF.class));
    assertNull(provider.getTypeAdapter(Inf3.class));
    assertNull(provider.getTypeAdapter(Inf1.class));
    assertNull(provider.getTypeAdapter(List.class));
  }

  @Test
  public void testSynchronized() throws Exception {
    LinkedHashMap<Class<?>, TypeAdapter<?>> typeAdapters = new LinkedHashMap<>();
    typeAdapters.put(DummyB.class, new TypeAdapter1());
    AdapterProvider provider = new CacheableAdapterProvider(typeAdapters);
    CountDownLatch count = new CountDownLatch(3);
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    AtomicBoolean success = new AtomicBoolean(true);

    for (int i = 0; i < 3; i++) {
      executorService.submit(
          () -> {
            if (provider.getTypeAdapter(DummyB.class) == null) success.set(false);
            if (provider.getTypeAdapter(ArrayList.class) != null) success.set(false);
            count.countDown();
          });
    }

    count.await();
    executorService.shutdown();
    assertTrue(success.get());
  }

  private static class DummyA {}

  private static class DummyB extends DummyA {}

  private static class DummyC extends DummyA implements Inf1 {}

  private static class DummyD extends DummyB implements Inf2 {}

  private static class DummyE extends DummyB {}

  private static class DummyF extends DummyC implements Inf1, Inf2 {}

  private static class DummyG extends DummyE implements Inf3 {}

  private static class DummyH extends DummyF {}

  private static class DummyI extends DummyF {}

  private interface Inf1 {}

  private interface Inf2 extends Inf3 {}

  private interface Inf3 {}

  private static class TypeAdapter1 implements TypeAdapter<Object> {
    @Override
    public @Nullable Object simplify(
        @NotNull Context ctx, @NotNull Class<?> sourceType, @NotNull Object value)
        throws Exception {
      return null;
    }

    @Override
    public @Nullable Object complexify(
        @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
      return null;
    }
  }

  private static class TypeAdapter2 implements TypeAdapter<Object> {
    @Override
    public @Nullable Object simplify(
        @NotNull Context ctx, @NotNull Class<?> sourceType, @NotNull Object value)
        throws Exception {
      return null;
    }

    @Override
    public @Nullable Object complexify(
        @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
      return null;
    }
  }

  private static class TypeAdapter3 implements TypeAdapter<Object> {
    @Override
    public @Nullable Object simplify(
        @NotNull Context ctx, @NotNull Class<?> sourceType, @NotNull Object value)
        throws Exception {
      return null;
    }

    @Override
    public @Nullable Object complexify(
        @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
      return null;
    }
  }
}
