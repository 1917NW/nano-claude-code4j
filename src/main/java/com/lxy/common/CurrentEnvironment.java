package com.lxy.common;

public class CurrentEnvironment {

    public static String WORK_DIR;

    public static boolean log;

    static {
        WORK_DIR = System.getProperty("user.dir");
        log = true;
    }

}
