package com.dao.utils;

import java.io.File;

public class Misc {
    public static String currentLocation(String name) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(name).getPath();
    }

    public static String tabulate(String string){
        string = "\t" + string;
        return string.replace("\n", "\n\t");
    }

    public static String getConfigLocation(){
        return currentLocation("conf");
    }

    public static String getConnectionConfLocation(){
        String separator = File.separator;
        return getConfigLocation() + separator + "connection";
    }
}
