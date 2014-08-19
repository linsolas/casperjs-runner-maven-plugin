package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.LogUtils.getLogger;
import static com.github.linsolas.casperjsrunner.OSUtils.isWindows;
import static com.github.linsolas.casperjsrunner.toolchain.CasperjsToolchain.KEY_CASPERJS_TYPE;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;

public class CasperJsRuntimeFinder {

    public static String findCasperRuntime(final ToolchainManager toolchainManager,
            final MavenSession session, final String casperExecPath) {
        String result = null;

        getLogger().debug("Finding casperjs runtime ...");

        getLogger().debug("Trying from toolchain");
        final Toolchain tc = toolchainManager.getToolchainFromBuildContext(KEY_CASPERJS_TYPE, session);
        if (tc != null) {
            getLogger().debug("Toolchain in casperjs-plugin: " + tc);
            if (casperExecPath != null) {
                getLogger().warn(
                        "Toolchains are ignored, 'casperRuntime' parameter is set to " + casperExecPath);
                result = casperExecPath;
            } else {
                getLogger().debug("Found from toolchain");
                result = tc.findTool(KEY_CASPERJS_TYPE);
            }
        }

        if (result == null) {
            getLogger().debug("No toolchain found, falling back to parameter");
            result = casperExecPath;
        }

        if (result == null) {
            String defaultCasperRuntime = "casperjs";
            if (isWindows()) {
                defaultCasperRuntime = "casperjs.bat";
            }
            getLogger().debug(
                    "No parameter specified, falling back to default '" + defaultCasperRuntime + "'");
            result = defaultCasperRuntime;
        }

        return result;
    }

}
