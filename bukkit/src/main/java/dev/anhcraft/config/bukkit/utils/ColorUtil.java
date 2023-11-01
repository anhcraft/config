package dev.anhcraft.config.bukkit.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(([0-9a-fA-F]{6})|([0-9a-fA-F]{3}))");


    public static String colorize(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str.length());
        Matcher matcher = HEX_PATTERN.matcher(str);
        while (matcher.find()) {
            String hex = matcher.group();
            if(hex.length() == 5) {
                hex += hex.substring(2);
            }
            ChatColor color = ChatColor.of(hex.substring(1));
            matcher.appendReplacement(sb, color.toString());
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }
}
