package dev.anhcraft.config.bukkit.utils;

import dev.anhcraft.config.bukkit.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
        i.skullOwner(m.getOwner());
        if (NMSVersion.current().atLeast(NMSVersion.v1_18_R1)) {
            if (m.getOwnerProfile() != null) {
                i.skullTexture(m.getOwnerProfile().getTextures().getSkin());
            }
        }
    }, (i, im) -> {
        SkullMeta m = (SkullMeta) im;
        m.setOwner(i.skullOwner());
        if (NMSVersion.current().atLeast(NMSVersion.v1_18_R1)) {
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            profile.getTextures().setSkin(i.skullTexture());
            m.setOwnerProfile(profile);
        }
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
