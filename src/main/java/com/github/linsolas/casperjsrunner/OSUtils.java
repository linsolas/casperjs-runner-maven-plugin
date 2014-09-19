package com.github.linsolas.casperjsrunner;

import static java.lang.System.getProperty;

public class OSUtils {

    public static boolean isWindows() {
        return getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }
}
