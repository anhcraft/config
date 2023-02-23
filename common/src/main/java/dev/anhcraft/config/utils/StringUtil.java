package dev.anhcraft.config.utils;

public class StringUtil {
    // https://github.com/anhcraft/Neep/blob/master/src/main/java/dev/anhcraft/neep/utils/MathUtil.java
    public static boolean isNumber(String num){
        if(num == null || num.isEmpty()) {
            return false;
        }
        // 0: negative/positive mask (optional)
        // 1: number
        // 2: number (optional; if there was a dot)
        byte part = 0;
        char[] chars = num.toCharArray();
        boolean fraction = false;
        for(char c : chars){
            if(part == 0){
                if(Character.isDigit(c)){
                    part = 1;
                } else if(c == '.') {
                    part = 2;
                } else if(c == '-' || c == '+') {
                    part = 1;
                } else {
                    return false;
                }
            } else {
                if(c == '.'){
                    if(part == 1){
                        part = 2;
                    } else {
                        return false;
                    }
                } else if(Character.isDigit(c)){
                    if(part == 2){
                        fraction = true;
                    }
                } else {
                    return false;
                }
            }
        }
        return part != 2 || fraction;
    }
}
