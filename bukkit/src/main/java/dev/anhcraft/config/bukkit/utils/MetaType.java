package dev.anhcraft.config.bukkit.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
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
        Color c = m.getColor();
        i.leatherColorRed(c.getRed());
        i.leatherColorGreen(c.getGreen());
        i.leatherColorBlue(c.getBlue());
    }, (i, im) -> {
        LeatherArmorMeta m = (LeatherArmorMeta) im;
        m.setColor(Color.fromRGB(
                i.leatherColorRed(),
                i.leatherColorGreen(),
                i.leatherColorBlue()
        ));
    }),
    SKULL((i, im) -> {
        SkullMeta m = (SkullMeta) im;
        i.skullOwner(m.getOwner());
    }, (i, im) -> {
        SkullMeta m = (SkullMeta) im;
        m.setOwner(i.skullOwner());
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
        m.setTitle(title == null ? null : ChatColor.translateAlternateColorCodes('&', title));
        m.setAuthor(i.bookAuthor());
        m.setGeneration(i.bookGeneration());
        List<String> pages = i.bookPages();
        m.setPages(pages == null ? new ArrayList<>() : pages.stream()
                .map(p -> ChatColor.translateAlternateColorCodes('&', p))
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
