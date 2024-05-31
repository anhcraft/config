package dev.anhcraft.config;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.ComplexTypes;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

/**
 * An instance factory provides facilities to assemble instances.
 */
public final class InstanceFactory implements InstanceAssembler {
  private static Unsafe unsafe;

  static {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      unsafe = (Unsafe) f.get(null);
    } catch (IllegalAccessException | NoSuchFieldException ignored) {
    }
  }

  private final Map<Class<?>, InstanceAssembler> instanceAssemblers;
  private final Map<Class<?>, InstanceAssembler> onDemandCache;

  public InstanceFactory(@NotNull Map<Class<?>, InstanceAssembler> instanceAssemblers) {
    this.instanceAssemblers = instanceAssemblers;
    this.onDemandCache =
        new LinkedHashMap<>() {
          @Override
          protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100; // TODO let this configurable
          }
        };
  }

  /**
   * Gets the instance assembler for the given class.<br>
   * The assembler could be registered when building the {@link ConfigFactory}. If it is not found, a new instance
   * assembler is created on demand and cached for later uses. The default assembler attempts to utilize the
   * default constructor (no args), otherwise, it allocates a new instance on the heap without invoking any constructor.
   * @param clazz the class
   * @return the instance assembler
   * @param <T> the type
   */
  public <T> @NotNull InstanceAssembler getInstanceAssembler(@NotNull Class<T> clazz) {
    if (clazz == this.getClass())
      throw new IllegalArgumentException("Cannot instantiate InstanceFactory");

    InstanceAssembler ic = instanceAssemblers.get(clazz);
    if (ic != null) return ic;

    // If there is no instance assembler provided beforehand, the current class may not be
    // instantiable normally.
    if (!ComplexTypes.isInstantiable(clazz))
      throw new IllegalArgumentException("Cannot instantiate " + clazz.getName());

    ic = onDemandCache.get(clazz);
    if (ic != null) return ic;

    try {
      Constructor<T> c = clazz.getDeclaredConstructor();
      c.setAccessible(true);
      ic =
          new InstanceAssembler() {
            @Override
            public <V> @NotNull V newInstance(@NotNull Context context, @NotNull Class<V> clazz)
                throws Exception {
              //noinspection unchecked
              return (V) c.newInstance();
            }
          };
      onDemandCache.put(clazz, ic);
      return ic;
    } catch (NoSuchMethodException ignored) {
    }

    ic =
        new InstanceAssembler() {
          @Override
          public <V> @NotNull V newInstance(@NotNull Context context, @NotNull Class<V> clazz)
              throws Exception {
            //noinspection unchecked
            return (V) unsafe.allocateInstance(clazz);
          }
        };
    onDemandCache.put(clazz, ic);

    return ic;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> @NotNull T newInstance(@NotNull Context context, @NotNull Class<T> clazz)
      throws Exception {
    return getInstanceAssembler(clazz).newInstance(context, clazz);
  }
}
