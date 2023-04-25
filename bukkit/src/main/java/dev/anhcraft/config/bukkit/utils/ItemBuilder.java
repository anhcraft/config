package dev.anhcraft.config.bukkit.utils;

import com.google.common.collect.Multimap;
import dev.anhcraft.config.annotations.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Configurable
public class ItemBuilder implements Serializable {
    private static final long serialVersionUID = 7808305902298157946L;
    private final static String META_TYPE = "meta.type";
    private final static String META_POTION_TYPE = "meta.potion.type";
    private final static String META_POTION_EXTENDED = "meta.potion.extended";
    private final static String META_POTION_UPGRADED = "meta.potion.upgraded";
    private final static String META_LEATHER_COLOR_R = "meta.leather.color_r";
    private final static String META_LEATHER_COLOR_G = "meta.leather.color_g";
    private final static String META_LEATHER_COLOR_B = "meta.leather.color_b";
    private final static String META_SPAWN_EGG_ENTITY = "meta.spawn_egg.entity";
    private final static String META_SKULL_OWNER = "meta.skull.owner";
    private final static String META_BOOK_AUTHOR = "meta.book.author";
    private final static String META_BOOK_TITLE = "meta.book.title";
    private final static String META_BOOK_GENERATION = "meta.book.generation";
    private final static String META_BOOK_PAGES = "meta.book.pages";

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

    @Path(value = "modifiers")
    @Description("List of attribute modifiers")
    private List<ItemModifier> itemModifiers;

    @Path(META_TYPE)
    @Description("Item meta type")
    private MetaType metaType;

    @Path(META_POTION_TYPE)
    @Description({
            "Set the potion type",
            "Required item meta: potion"
    })
    private PotionType potionType;

    @Path(META_POTION_EXTENDED)
    @Description({
            "Set the 'extended' status",
            "Required item meta: potion"
    })
    private boolean potionExtended;

    @Path(META_POTION_UPGRADED)
    @Description({
            "Set the 'upgraded' status",
            "Required item meta: potion"
    })
    private boolean potionUpgraded;

    @Path(META_LEATHER_COLOR_R)
    @Description({
            "Set the leather color's red value",
            "Required item meta: leather"
    })
    private int leatherColorRed;

    @Path(META_LEATHER_COLOR_G)
    @Description({
            "Set the leather color's green value",
            "Required item meta: leather"
    })
    private int leatherColorGreen;

    @Path(META_LEATHER_COLOR_B)
    @Description({
            "Set the leather color's blue value",
            "Required item meta: leather"
    })
    private int leatherColorBlue;

    @Path(META_SKULL_OWNER)
    @Description({
            "Set the skull owner",
            "Required item meta: skull"
    })
    private String skullOwner;

    @Path(META_BOOK_TITLE)
    @Description({
            "Set the title of the book",
            "Required item meta: book"
    })
    private String bookTitle;

    @Path(META_BOOK_AUTHOR)
    @Description({
            "Set the author of the book",
            "Required item meta: book"
    })
    private String bookAuthor;

    @Path(META_BOOK_GENERATION)
    @Description({
            "Set the generation of the book",
            "Required item meta: book"
    })
    private BookMeta.Generation bookGeneration;

    @Path(META_BOOK_PAGES)
    @Description({
            "Set the pages of the book",
            "Required item meta: book"
    })
    private List<String> bookPages;

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
                pi.customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
                Multimap<Attribute, AttributeModifier> attr = meta.getAttributeModifiers();
                if (attr != null) {
                    pi.itemModifiers = new ArrayList<>();
                    for (Map.Entry<Attribute, AttributeModifier> entry : attr.entries()) {
                        pi.itemModifiers.add(new ItemModifier(entry.getKey(), entry.getValue()));
                    }
                }
                if (meta instanceof PotionMeta) pi.metaType = MetaType.POTION;
                else if (meta instanceof LeatherArmorMeta) pi.metaType = MetaType.LEATHER;
                else if (meta instanceof SkullMeta) pi.metaType = MetaType.SKULL;
                else if (meta instanceof BookMeta) pi.metaType = MetaType.BOOK;
                if (pi.metaType != null) {
                    pi.metaType.getOnLoad().accept(pi, meta);
                }
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

    @Nullable
    public List<ItemModifier> itemModifiers() {
        return itemModifiers;
    }

    public void itemModifiers(@Nullable List<ItemModifier> itemModifiers) {
        this.itemModifiers = itemModifiers;
    }

    @Nullable
    public MetaType metaType() {
        return metaType;
    }

    public void metaType(@Nullable MetaType metaType) {
        this.metaType = metaType;
    }

    public boolean potionExtended() {
        return potionExtended;
    }

    public void potionExtended(boolean potionExtended) {
        this.potionExtended = potionExtended;
    }

    public boolean potionUpgraded() {
        return potionUpgraded;
    }

    public void potionUpgraded(boolean potionUpgraded) {
        this.potionUpgraded = potionUpgraded;
    }

    @Nullable
    public PotionType potionType() {
        return potionType;
    }

    public void potionType(@Nullable PotionType potionType) {
        this.potionType = potionType;
    }

    public int leatherColorRed() {
        return leatherColorRed;
    }

    public void leatherColorRed(int leatherColorRed) {
        this.leatherColorRed = leatherColorRed;
    }

    public int leatherColorGreen() {
        return leatherColorGreen;
    }

    public void leatherColorGreen(int leatherColorGreen) {
        this.leatherColorGreen = leatherColorGreen;
    }

    public int leatherColorBlue() {
        return leatherColorBlue;
    }

    public void leatherColorBlue(int leatherColorBlue) {
        this.leatherColorBlue = leatherColorBlue;
    }

    @Nullable
    public String skullOwner() {
        return skullOwner;
    }

    public void skullOwner(@Nullable String skullOwner) {
        this.skullOwner = skullOwner;
    }

    @Nullable
    public String bookTitle() {
        return bookTitle;
    }

    public void bookTitle(@Nullable String bookTitle) {
        this.bookTitle = bookTitle;
    }

    @Nullable
    public String bookAuthor() {
        return bookAuthor;
    }

    public void bookAuthor(@Nullable String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    @Nullable
    public BookMeta.Generation bookGeneration() {
        return bookGeneration;
    }

    public void bookGeneration(@Nullable BookMeta.Generation bookGeneration) {
        this.bookGeneration = bookGeneration;
    }

    @Nullable
    public List<String> bookPages() {
        return bookPages;
    }

    public void bookPages(@Nullable List<String> bookPages) {
        this.bookPages = bookPages;
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
            if (itemModifiers != null) {
                for (ItemModifier itemModifier : itemModifiers) {
                    meta.addAttributeModifier(itemModifier.getAttribute(), itemModifier.getModifier());
                }
            }
            if (metaType != null) {
                metaType.getOnSave().accept(this, meta);
            }
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
