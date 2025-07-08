package io.rong.imkit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    public static boolean isJSON(String jsonStr) {
        String regEx = "^\\{(.*?)\\}|^\\[(.*?)\\]$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(jsonStr.trim());
        return matcher.matches();
    }
}
