package dev.anhcraft.config.bukkit.utils;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Validation;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

@Configurable
public class ItemModifier {
    @Description("The attribute type")
    @Validation(notNull = true)
    private Attribute attribute;

    @Description("The modifier")
    @Validation(notNull = true)
    private AttributeModifier modifier;

    public ItemModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        this.attribute = attribute;
        this.modifier = modifier;
    }

    @NotNull
    public Attribute getAttribute() {
        return attribute;
    }

    @NotNull
    public AttributeModifier getModifier() {
        return modifier;
    }
}
