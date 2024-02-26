package dev.anhcraft.config;

import dev.anhcraft.config.SettingFlag.Normalizer;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.adapter.TypeAnnotator;
import dev.anhcraft.config.blueprint.Property;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.ElementScope;
import dev.anhcraft.config.context.PropertyScope;
import dev.anhcraft.config.error.IllegalTypeException;
import dev.anhcraft.config.type.SimpleTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

/**
 * The normalizer simplifies a complex object with the following rules:
 * <ul>
 *     <li>{@code null} is returned as is</li>
 *     <li>Primitive, primitive wrappers, String and {@link Dictionary} are returned as is</li>
 *     <li>Array is recursively checked with each element normalized independently</li>
 *     <li>Common reference types are normalized using the built-in type adapters</li>
 * </ul>
 * For the rest of reference types, Config tries to normalize it automatically using the schema generated by the
 * current {@link ConfigFactory}. Developers may use {@link TypeAdapter} to have further fine-grained control over
 * the normalization process. Failing to normalize an object will result in {@code null}.
 * It is possible to encapsulate the output by specifying the superclass as the target type. This results in implicit
 * creation of the schema of the superclass. The result hides the details of the actual class and only contains the
 * information since the superclass up to the root of class hierarchy.
 * @see SimpleTypes#test(Object)
 * @see TypeAdapter
 */
public final class ConfigNormalizer {
    private final ConfigFactory configFactory;
    private final byte settings;

    public ConfigNormalizer(ConfigFactory configFactory, byte settings) {
        this.configFactory = configFactory;
        this.settings = settings;
    }

    public byte getSettings() {
        return settings;
    }

    /**
     * Normalizes the given complex object into a simple object.<br>
     * This creates a new {@link Context} to facilitate recursive calls.
     * @param complex the complex object
     * @return the simple object or {@code null} if the object cannot be normalized
     * @param <T> the type of the simple object
     * @throws Exception may throw exceptions during normalization
     * @see #normalize(Context, Class, Object)
     */
    public <T> @Nullable Object normalize(@NotNull T complex) throws Exception {
        return normalize(configFactory.createContext(), complex);
    }

    /**
     * Normalizes the given complex object into a simple object.
     * @param ctx the {@link Context} to use
     * @param complex the complex object
     * @return the simple object or {@code null} if the object cannot be normalized
     * @param <T> the type of the simple object
     * @throws Exception may throw exceptions during normalization
     * @see #normalize(Context, Class, Object)
     */
    public <T> @Nullable Object normalize(@NotNull Context ctx, @NotNull T complex) throws Exception {
        //noinspection unchecked
        return normalize(ctx, (Class<T>) complex.getClass(), complex);
    }

    /**
     * Normalizes the given complex object into a simple object.<br>
     * This normalizes {@code complex} using the schema of {@code type} and its superclasses. {@code <S>} can
     * be bounded to a certain supertype of {@code <T>} to make the simple object more generic. However, this
     * only works for the first layer of the transformation.
     * @param ctx the {@link Context} to use
     * @param type the class or superclass of the complex object
     * @param complex the complex object
     * @return the simple object or {@code null} if the object cannot be normalized
     * @param <S> the type or supertype of the complex object
     * @param <T> the type of the simple object
     * @throws Exception may throw exceptions during normalization
     */
    public <S, T extends S> @Nullable Object normalize(@NotNull Context ctx, @NotNull Class<S> type, @NotNull T complex) throws Exception {
        validateType(ctx, type, complex);
        return _normalize(ctx, type, complex);
    }

    /**
     * Normalizes the given complex object into the given dictionary.<br>
     * This creates a new {@link Context} to facilitate recursive calls.
     * @param complex the complex object
     * @param dictionary the dictionary
     * @param <T> the type of the complex object
     * @throws Exception may throw exceptions during normalization
     * @see #normalize(Context, Class, Object)
     */
    public <T> void normalizeToDictionary(@NotNull T complex, @NotNull Dictionary dictionary) throws Exception {
        //noinspection unchecked
        normalizeToDictionary(configFactory.createContext(), (Class<T>) complex.getClass(), complex, dictionary);
    }

    /**
     * Normalizes the given complex object into the given dictionary.
     * @param ctx the {@link Context} to use
     * @param complex the complex object
     * @param dictionary the dictionary
     * @param <T> the type of the complex object
     * @throws Exception may throw exceptions during normalization
     * @see #normalize(Context, Class, Object)
     */
    public <T> void normalizeToDictionary(@NotNull Context ctx, @NotNull T complex, @NotNull Dictionary dictionary) throws Exception {
        //noinspection unchecked
        normalizeToDictionary(ctx, (Class<T>) complex.getClass(), complex, dictionary);
    }

    /**
     * Normalizes the given complex object into the given dictionary.
     * @param ctx the {@link Context} to use
     * @param type the class or superclass of the complex object
     * @param complex the complex object
     * @param dictionary the dictionary
     * @param <S> the type or supertype of the complex object
     * @param <T> the type of the complex object
     * @throws Exception may throw exceptions during normalization
     * @see #normalize(Context, Class, Object)
     */
    public <S, T extends S> void normalizeToDictionary(@NotNull Context ctx, @NotNull Class<S> type, @NotNull T complex, @NotNull Dictionary dictionary) throws Exception {
        validateType(ctx, type, complex);
        _dynamicNormalize(ctx, type, complex, dictionary);
    }

    // ======== Internal implementations ========

    private <S, T extends S> void validateType(Context ctx, Class<S> type, T complex) {
        if (!type.isAssignableFrom(complex.getClass()))
            throw new IllegalTypeException(ctx, String.format("%s is not assignable from %s", type.getName(), complex.getClass().getName()));
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) // generic sucks
    private Object _normalize(Context ctx, Class<?> type, Object complex) throws Exception {
        if (SimpleTypes.test(complex)) {
            if (SettingFlag.has(settings, Normalizer.DEEP_CLONE))
                return SimpleTypes.deepClone(complex);
            return complex;
        }
        if (type.isArray()) {
            return _normalizeArray(ctx, complex);
        }
        TypeAdapter adapter = configFactory.getTypeAdapter(type);
        if (adapter != null && !(adapter instanceof TypeAnnotator)) {
            Object result = adapter.simplify(ctx, type, complex);
            if (!SimpleTypes.test(result)) {
                String msg = String.format("Simple type expected but got %s", result.getClass().getName());
                throw new IllegalTypeException(ctx, msg);
            }
            return result;
        }
        Dictionary container = new Dictionary();
        _dynamicNormalize(ctx, type, complex, container);
        return container;
    }

    private Object _normalizeArray(Context ctx, Object complex) throws Exception {
        int n = Array.getLength(complex);
        Object[] result = new Object[n];
        for (int i = 0; i < n; i++) {
            ctx.enterScope(new ElementScope(i));
            {
                Object elem = Array.get(complex, i);
                Class<?> clazz = elem.getClass();
                result[i] = _normalize(ctx, clazz, elem);
            }
            ctx.exitScope();
        }
        return result;
    }

    private void  _dynamicNormalize(Context ctx, Class<?> type, Object complex, Dictionary container) throws Exception {
        if (complex instanceof Dictionary) {
            if (SettingFlag.has(settings, Normalizer.DEEP_CLONE)) { // TODO reduce allocations
                container.putAll(SimpleTypes.deepClone((Dictionary) complex));
            } else {
                container.putAll((Dictionary) complex);
            }
            return;
        }

        Schema schema = ctx.getFactory().getSchema(type);
        for (Property property : schema.properties()) {
            if (property.isTransient())
                continue;

            ctx.enterScope(new PropertyScope(property, property.name()));
            scope:
            {

                // TODO should we have @Optional for wrapper-side?
                Object value = property.field().get(complex);
                if (value != null) {
                    value = _normalize(ctx, value.getClass(), value);
                }

                if (SettingFlag.has(settings, Normalizer.IGNORE_DEFAULT_VALUES) &&
                        value instanceof Number && Math.abs(((Number) value).floatValue()) < 1e-8)
                    break scope;
                if (SettingFlag.has(settings, Normalizer.IGNORE_DEFAULT_VALUES) &&
                        value instanceof Boolean && !((Boolean) value))
                    break scope;
                if (SettingFlag.has(settings, Normalizer.IGNORE_EMPTY_ARRAY) &&
                        value != null && value.getClass().isArray() && Array.getLength(value) == 0)
                    break scope;
                if (SettingFlag.has(settings, Normalizer.IGNORE_EMPTY_DICTIONARY) &&
                        value instanceof Dictionary && ((Dictionary) value).isEmpty())
                    break scope;

                if (SimpleTypes.test(value))
                    container.put(property.name(), value);
            }
            ctx.exitScope();
        }
    }
}
