package dev.anhcraft.config.bukkit.utils;

import dev.anhcraft.config.annotations.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Configurable
public class ItemBuilder implements Serializable {
    private static final long serialVersionUID = 7808305902298157946L;

    @Path(value = "material")
    @Description(value = {"The material that make up this item"})
    @Validation(notNull = true, silent = true)
    private Material material = Material.AIR;

    @Path(value = "amount")
    @Description(value = {"The amount of items in this stack"})
    private int amount = 1;

    @Path(value = "name")
    @Description(value = {"The name of this item"})
    private String name;

    @Path(value = "damage")
    @Description(value = {"The damaged value"})
    private int damage;

    @Path(value = "lore")
    @Description(value = {"Item's lore"})
    @Validation(notNull = true, silent = true)
    private List<String> lore = new ArrayList<>();

    @Path(value = "enchant")
    @Description(value = {"Item's enchantments"})
    @Validation(notNull = true, silent = true)
    private Map<Enchantment, Integer> enchants = new HashMap<>();

    @Path(value = "flag")
    @Description(value = {"Items's flags that used to hide something"})
    @Validation(notNull = true, silent = true)
    private List<ItemFlag> flags = new ArrayList<>();

    @Path(value = "customModelData")
    @Description(value = {
            "Custom model data",
            "Default: 0 to unset customModelData"
    })
    private int customModelData;

    @Path(value = "unbreakable")
    @Description(value = {"Make the item unbreakable"})
    private boolean unbreakable;

    @NotNull
    public static ItemBuilder of(@Nullable ItemStack itemStack) {
        ItemBuilder pi = new ItemBuilder();
        if (itemStack != null) {
            pi.material = itemStack.getType();
            pi.amount = itemStack.getAmount();
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                if (meta instanceof Damageable) {
                    pi.damage = ((Damageable) meta).getDamage();
                }
                if (meta.hasDisplayName()) {
                    pi.name = meta.getDisplayName();
                }
                if (meta.hasLore()) {
                    pi.lore = meta.getLore();
                }
                pi.flags = new ArrayList<>(meta.getItemFlags());
                pi.enchants = meta.getEnchants();
                pi.unbreakable = meta.isUnbreakable();
            }
        }
        return pi;
    }

    @NotNull
    public Material material() {
        return this.material;
    }

    public void material(@Nullable Material type) {
        this.material = type == null ? Material.AIR : type;
    }

    @Nullable
    public String name() {
        return this.name;
    }

    public void name(@Nullable String name) {
        this.name = name;
    }

    public int damage() {
        return this.damage;
    }

    public void damage(int damage) {
        this.damage = damage;
    }

    public int amount() {
        return this.amount;
    }

    public void amount(int amount) {
        this.amount = amount;
    }

    public boolean unbreakable() {
        return this.unbreakable;
    }

    public void unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    @NotNull
    public List<String> lore() {
        return this.lore;
    }

    public void lore(@Nullable List<String> lore) {
        if (lore == null) {
            this.lore.clear();
        } else {
            this.lore = lore;
        }
    }

    @NotNull
    public List<ItemFlag> flags() {
        return this.flags;
    }

    public void flags(@Nullable List<ItemFlag> flags) {
        if (flags == null) {
            this.flags.clear();
        } else {
            this.flags = flags;
        }
    }

    @NotNull
    public Map<Enchantment, Integer> enchants() {
        return this.enchants;
    }

    public void enchants(@Nullable Map<Enchantment, Integer> enchants) {
        if (enchants == null) {
            this.enchants.clear();
        } else {
            this.enchants = enchants;
        }
    }

    public ItemBuilder replaceDisplay(UnaryOperator<String> operator) {
        this.name = operator.apply(this.name);
        this.lore.replaceAll(operator);
        return this;
    }

    public int customModelData() {
        return this.customModelData;
    }

    public void customModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    @NotNull
    public ItemStack build() {
        ItemStack item = new ItemStack(this.material, this.amount, (short) this.damage);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (this.name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.name));
            }
            if (!this.lore.isEmpty()) {
                meta.setLore(this.lore.stream().map(lore -> ChatColor.translateAlternateColorCodes('&', lore)).collect(Collectors.toList()));
            }
            if (!this.flags.isEmpty()) {
                this.flags.stream().filter(Objects::nonNull).forEach(meta::addItemFlags);
            }
            if (!this.enchants.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> e : this.enchants.entrySet()) {
                    meta.addEnchant(e.getKey(), e.getValue(), true);
                }
            }
            meta.setUnbreakable(this.unbreakable);
            meta.setCustomModelData(customModelData == 0 ? null : customModelData);
            item.setItemMeta(meta);
        }
        return item;
    }

    @NotNull
    public ItemBuilder duplicate() {
        return this.copyTo(new ItemBuilder());
    }

    @Deprecated
    @NotNull
    public ItemBuilder merge(@NotNull ItemBuilder pi) {
        return this.copyTo(pi);
    }

    @NotNull
    public ItemBuilder copyTo(@NotNull ItemBuilder pi) {
        pi.name = this.name;
        pi.damage = this.damage;
        pi.amount = this.amount;
        pi.unbreakable = this.unbreakable;
        pi.material = this.material;
        pi.enchants.putAll(this.enchants);
        pi.flags.addAll(this.flags);
        pi.lore.addAll(this.lore);
        return pi;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ItemBuilder that = (ItemBuilder) o;
        return this.amount == that.amount && this.damage == that.damage && this.unbreakable == that.unbreakable && this.material == that.material && Objects.equals(this.name, that.name) && this.lore.equals(that.lore) && this.enchants.equals(that.enchants) && this.flags.equals(that.flags);
    }

    public int hashCode() {
        return Objects.hash(this.material, this.amount, this.name, this.damage, this.lore, this.enchants, this.flags, this.unbreakable);
    }
}
