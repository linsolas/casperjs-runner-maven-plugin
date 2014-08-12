package com.github.linsolas.casperjsrunner;

import java.io.File;

public class PathToNameBuilder {

    private static final String REPLACEMENT_CHAR = "_";

    public static String buildName(File rootDir, File path) {
        String result = path.getAbsolutePath();
        String rootPath = rootDir.getAbsolutePath();
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }

        if (!result.contains(rootPath)) {
            throw new IllegalArgumentException(path + " should be a child of " + rootDir);
        }

        result = result.replace(rootPath, "");
        result = result.replaceAll(File.separator, REPLACEMENT_CHAR);
        result = result.replaceAll("\\.", REPLACEMENT_CHAR);

        return result;
    }

}
