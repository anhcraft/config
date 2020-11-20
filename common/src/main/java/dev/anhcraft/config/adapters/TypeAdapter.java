package dev.anhcraft.config.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * The type adapter helps to convert "simple" types to "complex" types, and vice versa.
 * <ul>
 *     <li>Simple types: are what can be put in the configuration</li>
 *     <li>Complex types: are what in a deserialized object</li>
 * </ul>
 * For safety reasons, there is no parameter for "simple" types. All "simple" objects must be wrapped in {@link SimpleForm}.
 *
 * @param <ComplexT> "complex" type
 */
public interface TypeAdapter<ComplexT> {
    @Nullable
    SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull ComplexT value) throws Exception;

    @Nullable
    ComplexT complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception;
}
