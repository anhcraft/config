package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionAdapter implements TypeAdapter<Collection<?>> {

    // this method can be inherited to add more collection types
    @NotNull
    protected Collection<?> createCollectionOf(@NotNull Type type) {
        if (type instanceof ParameterizedType) {
            // given X<? extends Y> returns X
            type = ((ParameterizedType) type).getRawType();
        }
        try {
            Object o = ObjectUtil.newInstance((Class<?>) type);
            if (o instanceof Collection) {
                return (Collection<?>) o;
            }
        } catch (InstantiationException ignored) {
        }
        if (((Class<?>) type).isAssignableFrom(LinkedHashSet.class)) return new HashSet<>();
        if (((Class<?>) type).isAssignableFrom(TreeSet.class)) return new TreeSet<>();
        if (((Class<?>) type).isAssignableFrom(Set.class)) return new HashSet<>();
        if (((Class<?>) type).isAssignableFrom(Vector.class)) return new Vector<>();
        if (((Class<?>) type).isAssignableFrom(Stack.class)) return new Stack<>();
        if (((Class<?>) type).isAssignableFrom(LinkedList.class)) return new LinkedList<>();
        if (((Class<?>) type).isAssignableFrom(CopyOnWriteArrayList.class)) return new CopyOnWriteArrayList<>();
        return new ArrayList<>();
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Collection<?> value) throws Exception {
        Type elemType;
        if (sourceType instanceof ParameterizedType) {
            elemType = ((ParameterizedType) sourceType).getActualTypeArguments()[0];
        } else {
            elemType = Object.class;
        }
        Object[] array = new Object[value.size()];
        int i = 0;
        for (Object elem : value) {
            SimpleForm v = serializer.transform(elemType, elem);
            if (v != null) {
                array[i++] = v.getObject();
            }
        }
        return SimpleForm.of(array);
    }

    @Override
    public @Nullable Collection<?> complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isArray()) {
            Type elemType;
            if (targetType instanceof ParameterizedType) {
                elemType = ((ParameterizedType) targetType).getActualTypeArguments()[0];
            } else {
                elemType = Object.class;
            }
            Object obj = Objects.requireNonNull(value.getObject());
            @SuppressWarnings("rawtypes")
            Collection collection = createCollectionOf(targetType);
            for (int i = 0; i < Array.getLength(obj); i++) {
                //noinspection unchecked
                collection.add(deserializer.transform(elemType, SimpleForm.of(Array.get(obj, i))));
            }
            return collection;
        }
        return null;
    }
}
