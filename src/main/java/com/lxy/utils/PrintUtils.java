package com.lxy.utils;

public class PrintUtils {
    public static void printLongString(String text) {
        int chunkSize = 500;
        for (int i = 0; i < text.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, text.length());
            System.out.println(text.substring(i, end));
        }
    }

}
