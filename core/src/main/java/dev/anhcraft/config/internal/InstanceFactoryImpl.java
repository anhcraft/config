package dev.anhcraft.config.internal;

import dev.anhcraft.config.InstanceAssembler;
import dev.anhcraft.config.InstanceFactory;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.SchemaCreationException;
import dev.anhcraft.config.type.ComplexTypes;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

@ApiStatus.Internal
public class InstanceFactoryImpl implements InstanceFactory {
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

  public InstanceFactoryImpl(@NotNull Map<Class<?>, InstanceAssembler> instanceAssemblers) {
    this.instanceAssemblers = instanceAssemblers;
    this.onDemandCache =
        new LinkedHashMap<>() {
          @Override
          protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 100; // TODO let this configurable
          }
        };
  }

  @Override
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
      try {
        c.setAccessible(true);
      } catch (InaccessibleObjectException | SecurityException e) {
        throw new SchemaCreationException(
            "Cannot setAccessible to constructor in " + c.getDeclaringClass().getName());
      }
      ic =
          new InstanceAssembler() {
            @Override
            public <V> @NotNull V newInstance(@NotNull Context context, @NotNull Class<V> clazz)
                throws InstantiationException {
              try {
                //noinspection unchecked
                return (V) c.newInstance();
              } catch (IllegalAccessException e) {
                throw new InstantiationException(
                    "Cannot access constructor declared in " + c.getDeclaringClass().getName());
              } catch (InvocationTargetException e) {
                throw new InstantiationException(
                    "Cannot invoke constructor declared in " + c.getDeclaringClass().getName());
              }
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
              throws InstantiationException {
            //noinspection unchecked
            return (V) unsafe.allocateInstance(clazz);
          }
        };
    onDemandCache.put(clazz, ic);

    return ic;
  }

  @Override
  public <T> @NotNull T newInstance(@NotNull Context context, @NotNull Class<T> clazz)
      throws InstantiationException {
    return getInstanceAssembler(clazz).newInstance(context, clazz);
  }
}
