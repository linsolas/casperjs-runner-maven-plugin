package fr.linsolas.casperjsrunner;

import static fr.linsolas.casperjsrunner.LogUtils.getLogger;
import static java.util.Arrays.asList;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.List;

public class ScriptsFinder {

    private File testsDir;

    private String specific;

    private List<String> patterns;

    public ScriptsFinder(File testsDir, String specific, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            throw new IllegalArgumentException("Patterns to search must be defined !");
        }
        this.testsDir = testsDir;
        this.specific = specific;
        this.patterns = patterns;
    }

    public List<String> findScripts() {
        getLogger().info("Looking for scripts in " + testsDir + "...");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setCaseSensitive(false);
        scanner.setBasedir(testsDir);
        if (specific != null && !specific.isEmpty()) {
            scanner.setIncludes(new String[] { specific });
        } else {
            scanner.setIncludes(patterns.toArray(new String[patterns.size()]));
        }
        scanner.scan();

        List<String> result = asList(scanner.getIncludedFiles());
        if (result.isEmpty()) {
            getLogger().warn("No files found in directory " + testsDir + " matching criterias");
        }

        return result;
    }

}
