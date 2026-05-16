package com.lxy.common;

import com.lxy.memory.MemoryManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CurrentEnvironment {

    public static String WORK_DIR;

    public static boolean log;

    public static final Properties APPLICATION_PROPERTIES = new Properties();

    public static void init(){
        WORK_DIR = System.getProperty("user.dir");
        log = true;
        loadApplicationProperties();
    }

    private static void loadApplicationProperties() {
        try (InputStream inputStream = CurrentEnvironment.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                APPLICATION_PROPERTIES.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }

    public static String getProperty(String key) {
        return APPLICATION_PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return APPLICATION_PROPERTIES.getProperty(key, defaultValue);
    }
}
