package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.LogUtils.getLogger;
import static java.util.Arrays.asList;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.List;

public class IncludesFinder {

    private File baseDir;

    private List<String> patterns;

    public IncludesFinder(File baseDir, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Patterns to search must be defined !");
        }
        this.baseDir = baseDir;
        this.patterns = patterns;
    }

    public List<String> findIncludes() {
        getLogger().info("Looking for includes in " + baseDir + "...");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setCaseSensitive(false);
        scanner.setBasedir(baseDir);
        scanner.setIncludes(patterns.toArray(new String[patterns.size()]));
        scanner.scan();

        return asList(scanner.getIncludedFiles());
    }

}
