package dev.anhcraft.config.bukkit.utils;

import com.google.common.collect.Multimap;
import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Path;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.config.bukkit.NMSVersion;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
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

@Configurable(keyNamingStyle = Configurable.NamingStyle.TRAIN_CASE)
public class ItemBuilder implements Serializable {
    private static final long serialVersionUID = 7808305902298157946L;
    private final static String META_TYPE = "meta.type";
    private final static String META_POTION_TYPE = "meta.potion-type";
    private final static String META_POTION_EXTENDED = "meta.potion-extended";
    private final static String META_POTION_UPGRADED = "meta.potion-upgraded";
    private final static String META_LEATHER_COLOR = "meta.leather-color";
    private final static String META_SKULL_TEXTURE = "meta.skull-texture";
    private final static String META_BOOK_AUTHOR = "meta.book-author";
    private final static String META_BOOK_TITLE = "meta.book-title";
    private final static String META_BOOK_GENERATION = "meta.book-generation";
    private final static String META_BOOK_PAGES = "meta.book-pages";
    private final static String META_STORED_ENCHANTMENTS = "meta.stored-enchantments";
    private final static String META_BANNER_PATTERNS = "meta.banner-patterns";
    private final static String META_TRIM_MATERIAL = "meta.trim-material";
    private final static String META_TRIM_PATTERN = "meta.trim-pattern";

    @Description(value = {"The material that make up this item"})
    @Validation(notNull = true, silent = true)
    private Material material = Material.AIR;

    @Description(value = {"The amount of items in this stack"})
    private int amount = 1;

    @Description(value = {"The name of this item"})
    private String name;

    @Description(value = {"The damaged value"})
    private int damage;

    @Description(value = {"Item's lore"})
    @Validation(notNull = true, silent = true)
    private List<String> lore = new ArrayList<>();

    @Description(value = {"Item's enchantments"})
    private Map<Enchantment, Integer> enchantments;

    @Description(value = {"Items's flags"})
    private List<ItemFlag> flags;

    @Description(value = {
            "Custom model data",
            "Default: 0 to unset customModelData"
    })
    private int customModelData;

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

    @Path(META_LEATHER_COLOR)
    @Description({
            "Set the leather color",
            "Required item meta: leather"
    })
    private Color leatherColor;

    @Path(META_SKULL_TEXTURE)
    @Description({
            "Set the skull texture",
            "Example: 27267165e42bf310afd1f12304f414d562624d64862873fb4aadee046a22a58a",
            "Or https://textures.minecraft.net/texture/27267165e42bf310afd1f12304f414d562624d64862873fb4aadee046a22a58a",
            "Required item meta: skull"
    })
    private String skullTexture;

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

    @Path(META_STORED_ENCHANTMENTS)
    @Description({
            "Set which enchantments are stored in the enchanted book",
            "Required item meta: enchanted_book"
    })
    private Map<Enchantment, Integer> storedEnchantments;

    @Path(META_BANNER_PATTERNS)
    @Description({
            "Set the pattern of the banner",
            "Required item meta: banner"
    })
    private List<Pattern> bannerPatterns;

    @Path(META_TRIM_PATTERN)
    @Description({
            "Set the pattern of the banner",
            "Required item meta: armor (1.20+)"
    })
    private String trimPattern;

    @Path(META_TRIM_MATERIAL)
    @Description({
            "Set the pattern of the banner",
            "Required item meta: armor (1.20+)"
    })
    private String trimMaterial;

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
                pi.enchantments = meta.getEnchants();
                pi.unbreakable = meta.isUnbreakable();
                pi.customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
                Multimap<Attribute, AttributeModifier> attr = meta.getAttributeModifiers();
                if (attr != null) {
                    pi.itemModifiers = new ArrayList<>();
                    for (Map.Entry<Attribute, AttributeModifier> entry : attr.entries()) {
                        pi.itemModifiers.add(new ItemModifier(entry.getKey(), entry.getValue()));
                    }
                }

                if (meta instanceof PotionMeta)
                    pi.metaType = MetaType.POTION;
                else if (meta instanceof LeatherArmorMeta)
                    pi.metaType = MetaType.LEATHER;
                else if (meta instanceof SkullMeta)
                    pi.metaType = MetaType.SKULL;
                else if (meta instanceof BookMeta)
                    pi.metaType = MetaType.BOOK;
                else if (meta instanceof BannerMeta)
                    pi.metaType = MetaType.BANNER;
                else if (meta instanceof EnchantmentStorageMeta)
                    pi.metaType = MetaType.ENCHANTED_BOOK;
                else if (NMSVersion.current().atLeast(NMSVersion.v1_20_R1) && meta instanceof org.bukkit.inventory.meta.ArmorMeta)
                    pi.metaType = MetaType.ARMOR;

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
        this.lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
    }

    @Nullable
    public List<ItemFlag> flags() {
        return this.flags;
    }

    public void flags(@Nullable List<ItemFlag> flags) {
        this.flags = flags == null ? null : new ArrayList<>(flags);
    }

    public ItemBuilder flag(@NotNull ItemFlag flag) {
        if (flags == null) flags = new ArrayList<>();
        flags.add(flag);
        return this;
    }

    @Nullable
    public Map<Enchantment, Integer> enchantments() {
        return this.enchantments;
    }

    public void enchantments(@Nullable Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments == null ? null : new HashMap<>(enchantments);
    }

    public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        if (enchantments == null) enchantments = new HashMap<>();
        enchantments.put(enchantment, level);
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
        this.itemModifiers = itemModifiers == null ? null : new ArrayList<>(itemModifiers);
    }

    public void addItemModifier(ItemModifier itemModifier) {
        if (itemModifiers == null) itemModifiers = new ArrayList<>();
        itemModifiers.add(itemModifier);
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

    @Nullable
    public Color leatherColor() {
        return leatherColor;
    }

    public void leatherColor(@Nullable Color leatherColor) {
        this.leatherColor = leatherColor;
    }

    @Nullable
    public String skullTexture() {
        return skullTexture;
    }

    public void skullTexture(@Nullable String skullTexture) {
        this.skullTexture = skullTexture;
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
        this.bookPages = bookPages == null ? null : new ArrayList<>(bookPages);
    }

    @Nullable
    public Map<Enchantment, Integer> storedEnchantments() {
        return storedEnchantments;
    }

    public void storedEnchantments(@Nullable Map<Enchantment, Integer> storedEnchantments) {
        this.storedEnchantments = storedEnchantments == null ? null : new HashMap<>(storedEnchantments);
    }

    @Nullable
    public List<Pattern> bannerPatterns() {
        return bannerPatterns;
    }

    public void bannerPatterns(@Nullable List<Pattern> bannerPatterns) {
        this.bannerPatterns = bannerPatterns == null ? null : new ArrayList<>(bannerPatterns);
    }

    @Nullable
    public String trimPattern() {
        return trimPattern;
    }

    public void trimPattern(@Nullable String trimPattern) {
        this.trimPattern = trimPattern;
    }

    @Nullable
    public String trimMaterial() {
        return trimMaterial;
    }

    public void trimMaterial(@Nullable String trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    public ItemBuilder replaceDisplay(@NotNull UnaryOperator<String> operator) {
        if (this.name != null) this.name = operator.apply(this.name);
        this.lore.replaceAll(operator);
        if (this.bookPages != null) this.bookPages.replaceAll(operator);
        return this;
    }

    @NotNull
    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta instanceof Damageable) {
                ((Damageable) meta).setDamage(damage);
            }
            if (name != null) {
                meta.setDisplayName((ColorUtil.colorize(name)));
            }
            if (!lore.isEmpty()) {
                meta.setLore(lore.stream().map(ColorUtil::colorize).collect(Collectors.toList()));
            }
            if (flags != null && !flags.isEmpty()) {
                flags.stream().filter(Objects::nonNull).forEach(meta::addItemFlags);
            }
            if (enchantments != null && !enchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
                    meta.addEnchant(e.getKey(), e.getValue(), true);
                }
            }
            meta.setUnbreakable(unbreakable);
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
        ItemBuilder builder = new ItemBuilder();
        builder.material = material;
        builder.amount = amount;
        builder.name = name;
        builder.damage = damage;
        builder.lore(lore);
        builder.enchantments(enchantments);
        builder.flags(flags);
        builder.customModelData = customModelData;
        builder.unbreakable = unbreakable;
        builder.itemModifiers(itemModifiers);
        builder.metaType = metaType;
        builder.skullTexture = skullTexture;
        builder.potionType = potionType;
        builder.potionUpgraded = potionUpgraded;
        builder.potionExtended = potionExtended;
        builder.leatherColor = leatherColor;
        builder.bookTitle = bookTitle;
        builder.bookPages(bookPages);
        builder.bookAuthor = bookAuthor;
        builder.bookGeneration = bookGeneration;
        builder.bannerPatterns(bannerPatterns);
        builder.storedEnchantments(storedEnchantments);
        builder.trimPattern = trimPattern;
        builder.trimMaterial = trimMaterial;
        return builder;
    }
}
