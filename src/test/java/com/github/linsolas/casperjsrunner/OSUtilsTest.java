package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.OSUtils.isWindows;
import static java.lang.System.setProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OSUtilsTest {

    @Test
    public void testIsWindows() {
        setProperty("os.name", "Linux");
        assertFalse(isWindows());

        setProperty("os.name", "Windows");
        assertTrue(isWindows());
    }
}
