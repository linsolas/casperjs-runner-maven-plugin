package com.github.linsolas.casperjsrunner;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class OrdererScriptsFinderDecoratorTest {

    @Mock
    private ScriptsFinder mockFinder;

    private OrdererScriptsFinderDecorator finder;

    @Before
    public void initializeFinder() {
        initMocks(this);

        finder = new OrdererScriptsFinderDecorator(mockFinder);
    }

    @Test
    public void testFindScripts() {
        when(mockFinder.findScripts()).thenReturn(
                asList("test1.js", "test1.coffee", "aTest.js", "subdir/aTest.js", "subdir/test.js", "anotherSubdir/test.coffee"));

        assertEquals(
                asList("aTest.js", "test1.coffee", "test1.js", "anotherSubdir/test.coffee", "subdir/aTest.js", "subdir/test.js"),
                newArrayList(finder.findScripts()));
    }
}
