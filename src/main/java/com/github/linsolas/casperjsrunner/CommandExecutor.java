package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.LogUtils.getLogger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.IOException;
import java.util.Map;

public class CommandExecutor {

    public static int executeCommand(final CommandLine line, final Map<String, String> environmentVariables,
            final boolean verbose) {
        getLogger().debug("Execute CasperJS command [" + line + "], with env: " + environmentVariables);
        try {
            final DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValues(new int[] { 0, 1 });
            return executor.execute(line, environmentVariables);
        } catch (final IOException e) {
            if (verbose) {
                getLogger().error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }

}
