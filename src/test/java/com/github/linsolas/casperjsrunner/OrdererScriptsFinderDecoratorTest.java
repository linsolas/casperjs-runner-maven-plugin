package com.github.linsolas.casperjsrunner;

import static com.google.common.collect.Lists.newArrayList;
import static java.io.File.separator;
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
                asList("test1.js", "test1.coffee", "aTest.js", "subdir" + separator + "aTest.js", "subdir" + separator + "test.js", "anotherSubdir" + separator + "test.coffee"));

        assertEquals(
                asList("aTest.js", "test1.coffee", "test1.js", "anotherSubdir" + separator + "test.coffee", "subdir" + separator + "aTest.js", "subdir" + separator + "test.js"),
                newArrayList(finder.findScripts()));
    }
}
