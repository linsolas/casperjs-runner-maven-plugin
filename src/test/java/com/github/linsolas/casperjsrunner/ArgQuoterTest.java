package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.ArgQuoter.quote;
import static java.lang.System.setProperty;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArgQuoterTest {

    @Test
    public void testQuoteNull() {
        assertEquals(null, quote(null));
    }

    @Test
    public void testQuoteEmptyStringOnLinux() {
        setProperty("os.name", "Linux");

        assertEquals("", quote(""));
    }

    @Test
    public void testQuoteEmptyStringOnWindows() {
        setProperty("os.name", "Windows");

        assertEquals("\"\"", quote(""));
    }

    @Test
    public void testQuoteSimpleString() {
        assertEquals("abc", quote("abc"));
    }

    @Test
    public void testQuoteComplexStringOnLinux() {
        setProperty("os.name", "Linux");

        assertEquals("a b\tc\\d\"e'f", quote("a b\tc\\d\"e'f"));
    }

    @Test
    public void testQuoteComplexStringOnWindows() {
        setProperty("os.name", "Windows");

        assertEquals("\"a b\tc\\d\\\"e'f\"", quote("a b\tc\\d\"e'f"));
    }
}
