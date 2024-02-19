package dev.anhcraft.config;

import dev.anhcraft.config.adapter.AdapterContext;
import dev.anhcraft.config.wrapper.SimpleTypes;
import org.jetbrains.annotations.NotNull;

public class ConfigNormalizer {
    private final ConfigFactory configFactory;

    public ConfigNormalizer(ConfigFactory configFactory) {
        this.configFactory = configFactory;
    }

    /**
     * Normalizes the given complex object into a simple object.<br>
     * This normalizes {@code complex} using the schema of {@code type} and its superclasses. {@code <S>} can
     * be bounded to a certain supertype of {@code <T>} to make the simple object more generic.<br>
     * This creates a new {@link AdapterContext} to facilitate recursive calls.
     * @param type the class or superclass of the complex object
     * @param complex the complex object
     * @return the simple object or {@code null} if the object cannot be normalized
     * @param <S> the type or supertype of the complex object
     * @param <T> the type of the simple object
     * @throws Exception may throw exceptions during normalization
     */
    public <S, T extends S> Object normalize(@NotNull Class<S> type, @NotNull T complex) throws Exception {
        return normalize(new AdapterContext(configFactory, 0), type, complex);
    }

    public <S, T extends S> Object normalize(AdapterContext ctx, Class<S> type, T complex) throws Exception {
        if (SimpleTypes.validate(complex))
            return complex;

    }
}
