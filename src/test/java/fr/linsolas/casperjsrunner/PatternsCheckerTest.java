package fr.linsolas.casperjsrunner;

import static fr.linsolas.casperjsrunner.PatternsChecker.checkPatterns;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.apache.maven.plugin.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class PatternsCheckerTest {

    @BeforeClass
    public static void initLog() throws IOException {
        LogUtils.setLog(mock(Log.class), false);
    }

    @Test
    public void testCheckNullPatterns() {
        assertEquals(
                asList("**/Test*.coffee", "**/*Test.coffee", "**/*TestCase.coffee", "**/Test*.js", "**/*Test.js", "**/*TestCase.js"),
                checkPatterns(null, true, true));
    }

    @Test
    public void testCheckEmptyPatterns() {
        assertEquals(
                asList("**/Test*.coffee", "**/*Test.coffee", "**/*TestCase.coffee", "**/Test*.js", "**/*Test.js", "**/*TestCase.js"),
                checkPatterns(new ArrayList<String>(), true, true));
    }

    @Test
    public void testCheckPatterns() {
        assertEquals(
                asList("pattern1", "pattern2"),
                checkPatterns(asList("pattern1", "pattern2"), true, true));
    }

    @Test
    public void testCheckOnlyJs() {
        assertEquals(
                asList("**/Test*.js", "**/*Test.js", "**/*TestCase.js"),
                checkPatterns(new ArrayList<String>(), true, false));
        assertEquals(
                asList("**/Test*.js"),
                checkPatterns(asList("**/Test*.js", "**/Test*.coffee"), true, false));
    }

    @Test
    public void testCheckOnlyCoffee() {
        assertEquals(
                asList("**/Test*.coffee", "**/*Test.coffee", "**/*TestCase.coffee"),
                checkPatterns(new ArrayList<String>(), false, true));
        assertEquals(
                asList("**/Test*.coffee"),
                checkPatterns(asList("**/Test*.js", "**/Test*.coffee"), false, true));
    }
}
