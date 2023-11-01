package dev.anhcraft.config.bukkit.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.anhcraft.config.bukkit.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
            i.skullTexture(properties.iterator().next().getValue());
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
            gameProfile.getProperties().put("textures", new Property("textures", texture, null));
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
