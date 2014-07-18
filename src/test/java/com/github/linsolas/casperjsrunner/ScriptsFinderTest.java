package com.github.linsolas.casperjsrunner;

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


public class ScriptsFinderTest {

    private static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), ScriptsFinderTest.class.getSimpleName());

    @BeforeClass
    public static void initLog() throws IOException {
        LogUtils.setLog(mock(Log.class), false);
    }

    @BeforeClass
    public static void createScripts() throws IOException {
        TEMP_DIR.mkdir();
        new File(TEMP_DIR, "test1.js").createNewFile();
        new File(TEMP_DIR, "test1.coffee").createNewFile();
        new File(TEMP_DIR, "testSuite2.js").createNewFile();
        new File(TEMP_DIR, "testSuite2.coffee").createNewFile();
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
        new ScriptsFinder(TEMP_DIR, null, null).findScripts();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindScriptWithEmptyPatterns() {
        new ScriptsFinder(TEMP_DIR, null, new ArrayList<String>()).findScripts();
    }

    @Test
    public void testFindScript() {
        assertEquals(
                newHashSet("test1.js", "test1.coffee", "testSuite2.js", "testSuite2.coffee"),
                newHashSet(new ScriptsFinder(TEMP_DIR, null, asList("*.*")).findScripts()));
    }

    @Test
    public void testFindScriptWithFileName() {
        assertEquals(
                newHashSet("testSuite2.js"),
                newHashSet(new ScriptsFinder(TEMP_DIR, "testSuite2.js", asList("*.*")).findScripts()));
        assertEquals(
                newHashSet("test1.coffee"),
                newHashSet(new ScriptsFinder(TEMP_DIR, "test1.coffee", asList("*.*")).findScripts()));
    }

    @Test
    public void testFindScriptWithPatterns() {
        assertEquals(
                newHashSet("test1.js", "testSuite2.js"),
                newHashSet(new ScriptsFinder(TEMP_DIR, null, asList("*.js")).findScripts()));
        assertEquals(
                newHashSet("test1.coffee", "testSuite2.coffee"),
                newHashSet(new ScriptsFinder(TEMP_DIR, null, asList("*.coffee")).findScripts()));
        assertEquals(
                newHashSet("testSuite2.js", "testSuite2.coffee"),
                newHashSet(new ScriptsFinder(TEMP_DIR, null, asList("*Suite*.*")).findScripts()));
    }

    @Test
    public void testFindScriptWithFilenameAndPatterns() {
        assertEquals(
                newHashSet("testSuite2.coffee"),
                newHashSet(new ScriptsFinder(TEMP_DIR, "testSuite2.coffee", asList("*.js")).findScripts()));
        assertEquals(
                newHashSet("test1.js"),
                newHashSet(new ScriptsFinder(TEMP_DIR, "test1.js", asList("*.coffee")).findScripts()));
        assertEquals(
                newHashSet("test1.js"),
                newHashSet(new ScriptsFinder(TEMP_DIR, "test1.js", asList("*Suite*.*")).findScripts()));
    }
}
