package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.ComplexTypes;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;

public class IterableAdapter implements TypeAdapter<Iterable<?>> {
    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<Iterable<?>> sourceType, @NotNull Iterable<?> value) throws Exception {
        List<Object> list = new ArrayList<>();
        for (Object object : value) {
            //noinspection unchecked,rawtypes
            list.add(ctx.simplify(ctx, (Class) object.getClass(), object));
        }
        return list.toArray();
    }

    @Override
    public @Nullable Iterable<?> complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value.getClass().isArray()) {
            Type componentType = ComplexTypes.getActualType(targetType, 0);
            if (componentType == null)
                return null;

            int length = Array.getLength(value);
            Class<?> targetClazz = ComplexTypes.erasure(targetType);
            Collection<Object> collection;

            if (LinkedList.class.isAssignableFrom(targetClazz)) {
                collection = new LinkedList<>();
            } else if (Queue.class.isAssignableFrom(targetClazz)) {
                if (Deque.class.isAssignableFrom(targetClazz)) {
                    collection = new ArrayDeque<>(length);
                } else {
                    collection = new PriorityQueue<>(length);
                }
            } else if (Set.class.isAssignableFrom(targetClazz)) {
                if (SortedSet.class.isAssignableFrom(targetClazz)) {
                    collection = new TreeSet<>();
                } else if (LinkedHashSet.class.isAssignableFrom(targetClazz)) {
                    collection = new LinkedHashSet<>(length);
                } else {
                    collection = new HashSet<>(length);
                }
            } else if (Stack.class.isAssignableFrom(targetClazz)) {
                collection = new Stack<>();
            } else if (Vector.class.isAssignableFrom(targetClazz)) {
                collection = new Vector<>(length);
            } else {
                collection = new ArrayList<>(length);
            }

            for (int i = 0; i < length; i++)
                collection.add(ctx.complexify(ctx, Array.get(value, i), componentType));
            return collection;
        }
        return null;
    }
}
