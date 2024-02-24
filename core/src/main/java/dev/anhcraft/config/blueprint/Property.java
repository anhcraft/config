package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.validate.Validator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Property {
    public static final byte MODIFIER_OPTIONAL = 1;
    public static final byte MODIFIER_TRANSIENT = 2;
    public static final byte MODIFIER_CONSTANT = 4;

    private final PropertyNaming naming;
    private final List<String> description;
    private final byte modifier; // optional, transient, constant
    private final Validator validator;
    private final Field field;

    public Property(@NotNull PropertyNaming naming, @NotNull List<String> description, byte modifier, @NotNull Validator validator, @NotNull Field field) {
        this.naming = naming;
        this.description = Collections.unmodifiableList(description);
        this.modifier = modifier;
        this.validator = validator;
        this.field = field;
    }

    @NotNull
    public String name() {
        return naming.primary();
    }

    @NotNull
    public Set<String> aliases() {
        return naming.aliases();
    }

    @NotNull
    public List<String> description() {
        return description;
    }

    public byte modifier() {
        return modifier;
    }

    public boolean isOptional() {
        return (modifier & MODIFIER_OPTIONAL) == MODIFIER_OPTIONAL;
    }

    public boolean isTransient() {
        return (modifier & MODIFIER_TRANSIENT) == MODIFIER_TRANSIENT;
    }

    public boolean isConstant() {
        return (modifier & MODIFIER_CONSTANT) == MODIFIER_CONSTANT;
    }

    @NotNull
    public Validator validator() {
        return validator;
    }

    @NotNull
    public Type type() {
        return field.getGenericType();
    }

    @NotNull
    public Field field() {
        return field;
    }
}
