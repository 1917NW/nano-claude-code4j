package com.lxy.common;

public class StringCaseConverter {

    public static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            char prev = i > 0 ? chars[i - 1] : 0;
            char next = i < chars.length - 1 ? chars[i + 1] : 0;

            if (Character.isUpperCase(current)) {
                boolean prevIsLowerOrDigit = i > 0 && (Character.isLowerCase(prev) || Character.isDigit(prev));
                boolean nextIsLower = i < chars.length - 1 && Character.isLowerCase(next);

                if (i > 0 && (prevIsLowerOrDigit || nextIsLower)) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(current));
            } else {
                sb.append(current);
            }
        }

        return sb.toString();
    }

    public static String snakeToCamel(String str) {
        return snakeToCamel(str, false);
    }

    public static String snakeToPascal(String str) {
        return snakeToCamel(str, true);
    }

    private static String snakeToCamel(String str, boolean capitalizeFirst) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperNext = capitalizeFirst;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '_') {
                upperNext = true;
            } else {
                if (upperNext) {
                    sb.append(Character.toUpperCase(c));
                    upperNext = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }

        return sb.toString();
    }


}
