package fr.linsolas.casperjsrunner;

import static fr.linsolas.casperjsrunner.LogUtils.getLogger;
import static java.util.Arrays.asList;

import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptsFinder {

    private File baseDir;

    private String specific;

    private List<String> includes;
    private List<String> excludes;

    public ScriptsFinder(File baseDir, String specific, List<String> includes, List<String> excludes) {
        if (includes == null || includes.isEmpty()) {
            throw new IllegalArgumentException("Include patterns to search must be defined !");
        }
        if (excludes == null) {
            throw new IllegalArgumentException("Excludes patterns must not be null !");
        }
        this.baseDir = baseDir;
        this.specific = specific;
        this.includes = includes;
        this.excludes = excludes;
    }

    public List<String> findScripts() {
        getLogger().info("Looking for scripts in " + baseDir + "...");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setCaseSensitive(false);
        scanner.setBasedir(baseDir);
        if (specific != null && !specific.isEmpty()) {
            List<String> temp = new ArrayList<String>();
            if (specific.endsWith(".js") || specific.endsWith(".coffee")) {
                temp.add("**/"+specific);
            } else {
                temp.add("**/"+specific+".js");
                temp.add("**/"+specific+".coffee");
            }
            scanner.setIncludes(temp.toArray(new String[temp.size()]));
        } else {
            scanner.setIncludes(includes.toArray(new String[includes.size()]));
            scanner.setExcludes(excludes.toArray(new String[excludes.size()]));
        }
        scanner.scan();

        List<String> result = asList(scanner.getIncludedFiles());
        if (result.isEmpty()) {
            getLogger().warn("No files found in directory " + baseDir + " matching criterias");
        }

        return result;
    }

}
