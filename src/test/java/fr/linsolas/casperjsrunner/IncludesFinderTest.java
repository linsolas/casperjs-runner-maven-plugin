package fr.linsolas.casperjsrunner;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.logging.Log;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class IncludesFinderTest {

    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), IncludesFinderTest.class.getSimpleName());

    @BeforeClass
    public static void initLog() throws IOException {
        LogUtils.setLog(mock(Log.class), false);
    }

    @BeforeClass
    public static void createScripts() throws IOException {
        TEMP_DIR.mkdir();
        new File(TEMP_DIR, "1.inc.js").createNewFile();
        new File(TEMP_DIR, "test.js").createNewFile();
        new File(TEMP_DIR, "inc.js").createNewFile();
    }

    @AfterClass
    public static void deleteScripts() {
        for (File f : TEMP_DIR.listFiles()) {
            f.delete();
        }
        TEMP_DIR.delete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindScriptWithNullPatterns() {
        new IncludesFinder(TEMP_DIR, null).findIncludes();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindScriptWithEmptyPatterns() {
        new IncludesFinder(TEMP_DIR, new ArrayList<String>()).findIncludes();
    }

    @Test
    public void testFindScript() {
        assertEquals(
                newHashSet("1.inc.js", "inc.js", "test.js"),
                newHashSet(new IncludesFinder(TEMP_DIR, asList("*.*")).findIncludes()));
    }

    @Test
    public void testFindScriptWithPatterns() {
        assertEquals(
                newHashSet("1.inc.js"),
                newHashSet(new IncludesFinder(TEMP_DIR, asList("*.inc.js")).findIncludes()));
        assertEquals(
                newHashSet("1.inc.js", "inc.js"),
                newHashSet(new IncludesFinder(TEMP_DIR, asList("*inc.js")).findIncludes()));
    }
}
