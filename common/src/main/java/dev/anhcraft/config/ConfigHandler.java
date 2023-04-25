package dev.anhcraft.config;

import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.adapters.defaults.*;
import dev.anhcraft.config.utils.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.*;

public abstract class ConfigHandler {
    private final Map<Class<?>, TypeAdapter<?>> typeAdapters = new WeakHashMap<>();
    private final ConfigProvider configProvider;
    private boolean preferCustomArrayAdapter;
    private boolean callSuperAdapter = true;

    protected ConfigHandler(@NotNull ConfigProvider configProvider) {
        this.configProvider = configProvider;
        registerTypeAdapter(Enum.class, new EnumAdapter());
        registerTypeAdapter(Map.class, new MapAdapter());
        registerTypeAdapter(Collection.class, new CollectionAdapter());
        registerTypeAdapter(Character.class, new CharacterAdapter());
        registerTypeAdapter(Boolean.class, new BooleanAdapter());
        registerTypeAdapter(Byte.class, new ByteAdapter());
        registerTypeAdapter(Short.class, new ShortAdapter());
        registerTypeAdapter(Integer.class, new IntegerAdapter());
        registerTypeAdapter(Long.class, new LongAdapter());
        registerTypeAdapter(Float.class, new FloatAdapter());
        registerTypeAdapter(Double.class, new DoubleAdapter());
        registerTypeAdapter(char.class, new CharacterAdapter());
        registerTypeAdapter(boolean.class, new BooleanAdapter());
        registerTypeAdapter(byte.class, new ByteAdapter());
        registerTypeAdapter(short.class, new ShortAdapter());
        registerTypeAdapter(int.class, new IntegerAdapter());
        registerTypeAdapter(long.class, new LongAdapter());
        registerTypeAdapter(float.class, new FloatAdapter());
        registerTypeAdapter(double.class, new DoubleAdapter());
        registerTypeAdapter(UUID.class, new UUIDAdapter());
        registerTypeAdapter(URL.class, new URLAdapter());
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
        typeAdapters.put(clazz, typeAdapter);
    }

    @Nullable
    protected TypeAdapter<?> getTypeAdapter(@NotNull Class<?> clazz) {
        return typeAdapters.get(clazz);
    }

    protected boolean isCustomArrayAdapterPreferred() {
        return preferCustomArrayAdapter;
    }

    protected boolean shouldCallSuperAdapter() {
        return callSuperAdapter;
    }
}
