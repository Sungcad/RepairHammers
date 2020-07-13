package me.sungcad.repairhammers.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern PATTERN = Pattern.compile("(?<!\\\\)(#([a-fA-F0-9]{6}))");

    public static String translateColors(String input){
        input = ChatColor.translateAlternateColorCodes('&', input);
        Matcher matcher = PATTERN.matcher(input);
        if(matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do{
                matcher.appendReplacement(sb, ChatColor.of(matcher.group(1)).toString());
            }while(matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return input;
    }
}
