package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.toolchain.CasperjsToolchain.KEY_CASPERJS_TYPE;
import static java.lang.System.setProperty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CasperJsRuntimeFinderTest {

    @Mock
    private ToolchainManager toolchainManager;

    @Mock
    private Toolchain toolchain;

    @Mock
    private MavenSession session;

    @Before
    public void initializeMocks() {
        initMocks(this);

        LogUtils.setLog(mock(Log.class), false);
    }

    @Test
    public void testFindCasperJsRuntimeFromToolchain() {
        when(toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session)).thenReturn(toolchain);
        when(toolchain.findTool(KEY_CASPERJS_TYPE)).thenReturn("casperjs runtime from toolchain");

        assertEquals("casperjs runtime from toolchain",
                CasperJsRuntimeFinder.findCasperRuntime(toolchainManager, session, null));
    }

    @Test
    public void testFindCasperJsRuntimeFromToolchainButOverriden() {
        when(toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session)).thenReturn(toolchain);
        when(toolchain.findTool(KEY_CASPERJS_TYPE)).thenReturn("casperjs runtime from toolchain");

        assertEquals("casperjs runtime from parameter", CasperJsRuntimeFinder.findCasperRuntime(
                toolchainManager, session, "casperjs runtime from parameter"));
    }

    @Test
    public void testFindCasperJsRuntimeFromParameter() {
        when(toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session)).thenReturn(null);

        assertEquals("casperjs runtime from parameter", CasperJsRuntimeFinder.findCasperRuntime(
                toolchainManager, session, "casperjs runtime from parameter"));
    }

    @Test
    public void testFindCasperJsRuntimeFromDefault() {
        when(toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session)).thenReturn(null);
        setProperty("os.name", "Linux");

        assertEquals("casperjs", CasperJsRuntimeFinder.findCasperRuntime(toolchainManager, session, null));
    }

    @Test
    public void testFindCasperJsRuntimeFromDefaultOnWindows() {
        when(toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session)).thenReturn(null);
        setProperty("os.name", "Windows");

        assertEquals("casperjs.bat", CasperJsRuntimeFinder.findCasperRuntime(toolchainManager, session, null));
    }
}
