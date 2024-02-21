package dev.anhcraft.config.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    // A fast split algorithm that does not use regex, and works for empty fragments
    public static List<String> fastSplit(String str, char separator) {
        if (str.isEmpty())
            return List.of();
        List<String> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == separator) {
                list.add(buffer.toString());
                buffer.setLength(0);
            }
            buffer.append(c);
        }
        if (str.charAt(str.length() - 1) == separator) {
            list.add("");
        } else {
            list.add(buffer.toString());
        }
        return list;
    }
}
