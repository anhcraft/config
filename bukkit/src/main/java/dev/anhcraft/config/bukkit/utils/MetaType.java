package dev.anhcraft.config.bukkit.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.anhcraft.config.bukkit.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public enum MetaType {
    POTION((i, im) -> {
        PotionMeta m = (PotionMeta) im;
        PotionData pd = m.getBasePotionData();
        i.potionType(pd.getType());
        i.potionExtended(pd.isExtended());
        i.potionUpgraded(pd.isUpgraded());
    }, (i, im) -> {
        PotionMeta m = (PotionMeta) im;
        PotionType pt = i.potionType();
        if (pt != null) {
            m.setBasePotionData(new PotionData(
                    pt,
                    pt.isExtendable() && i.potionExtended(),

                    pt.isUpgradeable() && i.potionUpgraded()
            ));
        }
    }),
    LEATHER((i, im) -> {
        LeatherArmorMeta m = (LeatherArmorMeta) im;
        i.leatherColor(m.getColor());
    }, (i, im) -> {
        LeatherArmorMeta m = (LeatherArmorMeta) im;
        m.setColor(i.leatherColor());
    }),
    SKULL((i, im) -> {
        SkullMeta m = (SkullMeta) im;
        if (NMSVersion.current().atLeast(NMSVersion.v1_18_R1)) {
            org.bukkit.profile.PlayerProfile profile = m.getOwnerProfile();
            if (profile != null && profile.getTextures().getSkin() != null)
                i.skullTexture(profile.getTextures().getSkin().toString());
            return;
        }
        try {
            Field f = m.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            GameProfile profile = (GameProfile) f.get(m);
            Collection<Property> properties = profile.getProperties().get("textures");
            if (properties.isEmpty()) return;
            String json = properties.iterator().next().getValue();
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonObject.has("textures")) {
                JsonObject textures = jsonObject.getAsJsonObject();
                if (textures.has("SKIN")) {
                    JsonObject skin = textures.get("SKIN").getAsJsonObject();
                    if (skin.has("url")) {
                        i.skullTexture(skin.getAsJsonPrimitive("url").getAsString());
                    }
                }
            }
        } catch (Exception ignored) {}
    }, (i, im) -> {
        try {
            String texture = i.skullTexture();
            if (texture == null) return;
            if (!texture.contains("/"))
                texture = "https://textures.minecraft.net/texture/" + texture;
            SkullMeta m = (SkullMeta) im;
            if (NMSVersion.current().atLeast(NMSVersion.v1_18_R1)) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                profile.getTextures().setSkin(new URL(texture));
                m.setOwnerProfile(profile);
                return;
            }
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            texture = "{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}";
            String txt = Base64.getEncoder().encodeToString(texture.getBytes(StandardCharsets.UTF_8));
            gameProfile.getProperties().put("textures", new Property("textures", txt, null));
            Field f = m.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            f.set(m, gameProfile);
        } catch (Exception ignored) {}
    }),
    BOOK((i, im) -> {
        BookMeta m = (BookMeta) im;
        i.bookTitle(m.getTitle());
        i.bookAuthor(m.getAuthor());
        i.bookGeneration(m.getGeneration());
        i.bookPages(m.getPages());
    }, (i, im) -> {
        BookMeta m = (BookMeta) im;
        String title = i.bookTitle();
        m.setTitle(ColorUtil.colorize(title));
        m.setAuthor(i.bookAuthor());
        m.setGeneration(i.bookGeneration());
        List<String> pages = i.bookPages();
        m.setPages(pages == null ? new ArrayList<>() : pages.stream()
                .map(ColorUtil::colorize)
                .collect(Collectors.toList()));
    }),
    BANNER((i, im) -> {
        BannerMeta m = (BannerMeta) im;
        i.bannerPatterns(m.getPatterns());
    }, (i, im) -> {
        BannerMeta m = (BannerMeta) im;
        List<Pattern> patterns = i.bannerPatterns();
        if (patterns != null)
            m.setPatterns(patterns);
    }),
    ENCHANTED_BOOK((i, im) -> {
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) im;
        i.storedEnchantments(m.getStoredEnchants());
    }, (i, im) -> {
        EnchantmentStorageMeta m = (EnchantmentStorageMeta) im;
        Map<Enchantment, Integer> enc = i.storedEnchantments();
        if (enc != null) {
            for (Enchantment enchantment : m.getStoredEnchants().keySet()) {
                m.removeStoredEnchant(enchantment);
            }
            for (Map.Entry<Enchantment, Integer> entry : enc.entrySet()) {
                m.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
    }),
    ARMOR((i, im) -> {
        if (!NMSVersion.current().atLeast(NMSVersion.v1_20_R1)) return;
        org.bukkit.inventory.meta.ArmorMeta armorMeta = (org.bukkit.inventory.meta.ArmorMeta) im;
        if (!armorMeta.hasTrim()) return;
        i.trimMaterial(armorMeta.getTrim().getMaterial().getKey().toString());
        i.trimPattern(armorMeta.getTrim().getPattern().getKey().toString());
    }, (i, im) -> {
        if (!NMSVersion.current().atLeast(NMSVersion.v1_20_R1)) return;
        org.bukkit.inventory.meta.ArmorMeta armorMeta = (org.bukkit.inventory.meta.ArmorMeta) im;
        String material = i.trimMaterial();
        if (material == null) return;
        String pattern = i.trimPattern();
        if (pattern == null) return;
        armorMeta.setTrim(new ArmorTrim(
                requireNonNull(requireNonNull(Bukkit.getRegistry(TrimMaterial.class)).get(requireNonNull(NamespacedKey.fromString(material)))),
                requireNonNull(requireNonNull(Bukkit.getRegistry(TrimPattern.class)).get(requireNonNull(NamespacedKey.fromString(pattern))))
        ));
    });

    private final BiConsumer<ItemBuilder, ItemMeta> onLoad;
    private final BiConsumer<ItemBuilder, ItemMeta> onSave;

    MetaType(BiConsumer<ItemBuilder, ItemMeta> onLoad, BiConsumer<ItemBuilder, ItemMeta> onSave) {
        this.onLoad = onLoad;
        this.onSave = onSave;
    }

    public BiConsumer<ItemBuilder, ItemMeta> getOnLoad() {
        return onLoad;
    }

    public BiConsumer<ItemBuilder, ItemMeta> getOnSave() {
        return onSave;
    }
}
