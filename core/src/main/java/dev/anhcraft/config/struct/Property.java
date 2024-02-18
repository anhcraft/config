package dev.anhcraft.config.struct;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Property {
    private final String name;
    private final Field field;

    public Property(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public Type type() {
        return field.getType();
    }

    @NotNull
    public Field field() {
        return field;
    }
}
