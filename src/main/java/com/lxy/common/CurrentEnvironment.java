package com.lxy.common;

public class CurrentEnvironment {

    public static String WORK_DIR;

    static {
        WORK_DIR = System.getProperty("user.dir");
    }

}
