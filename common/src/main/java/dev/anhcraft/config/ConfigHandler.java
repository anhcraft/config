package dev.anhcraft.config;

import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.adapters.defaults.*;
import dev.anhcraft.config.utils.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ConfigHandler {
    private final Map<String, TypeAdapter<?>> typeAdapters = new HashMap<>();
    private final ConfigProvider configProvider;
    private boolean preferCustomListAdapter;
    private boolean preferCustomArrayAdapter;
    private boolean callSuperAdapter = true;

    protected ConfigHandler(@NotNull ConfigProvider configProvider) {
        this.configProvider = configProvider;
        registerTypeAdapter(Enum.class, new EnumAdapter());
        registerTypeAdapter(Map.class, new MapAdapter());
        registerTypeAdapter(Character.class, new CharacterAdapter());
        registerTypeAdapter(Boolean.class, new BooleanAdapter());
        registerTypeAdapter(Byte.class, new ByteAdapter());
        registerTypeAdapter(Short.class, new ShortAdapter());
        registerTypeAdapter(Integer.class, new IntegerAdapter());
        registerTypeAdapter(Long.class, new LongAdapter());
        registerTypeAdapter(Float.class, new FloatAdapter());
        registerTypeAdapter(Double.class, new DoubleAdapter());
        registerTypeAdapter(UUID.class, new UUIDAdapter());
    }

    /**
     * By default, {@link TypeAdapter} only takes effect on elements.<br>
     * Enable this to expand the effect up to the whole list, allows
     * you to modify the list, or transform it to another kind of object.<br>
     * If the {@link TypeAdapter} not found, the default action will be done.
     *
     * @param value {@code true} or {@code false}
     */
    public void preferCustomListAdapter(boolean value) {
        this.preferCustomListAdapter = value;
    }

    /**
     * By default, {@link TypeAdapter} only takes effect on elements.<br>
     * Enable this to expand the effect up to the whole array, allows
     * you to modify the array, or transform it to another kind of object.<br>
     * If the {@link TypeAdapter} not found, the default action will be done.
     *
     * @param value {@code true} or {@code false}
     */
    public void preferCustomArrayAdapter(boolean value) {
        this.preferCustomArrayAdapter = value;
    }

    /**
     * When a {@link TypeAdapter} is not found, the handler will try to find
     * {@link TypeAdapter} of the superclass, and so on until succeed, or
     * the superclass reaches {@link Object}.<br>
     * Disable this option can slightly improve the performance.
     *
     * @param value {@code true} or {@code false}
     */
    public void callSuperAdapter(boolean value) {
        this.callSuperAdapter = value;
    }

    @NotNull
    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    /**
     * Registers type adapter that takes effect in the current instance.<br>
     * If another type adapter exists for the given class, it will be overridden.
     *
     * @param clazz       the class of "complex" type
     * @param typeAdapter the type adapter
     * @param <T>         "complex" type
     */
    public <T> void registerTypeAdapter(@NotNull Class<T> clazz, @NotNull TypeAdapter<?> typeAdapter) {
        typeAdapters.put(ClassUtil.hashClass(clazz), typeAdapter);
    }

    @Nullable
    protected TypeAdapter<?> getTypeAdapter(@NotNull Class<?> clazz) {
        return typeAdapters.get(ClassUtil.hashClass(clazz));
    }

    protected boolean isCustomListAdapterPreferred() {
        return preferCustomListAdapter;
    }

    protected boolean isCustomArrayAdapterPreferred() {
        return preferCustomArrayAdapter;
    }

    protected boolean shouldCallSuperAdapter() {
        return callSuperAdapter;
    }
}
