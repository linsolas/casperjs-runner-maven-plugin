package com.github.linsolas.casperjsrunner.toolchain;

import org.apache.maven.toolchain.DefaultToolchain;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Based on {@code org.apache.maven.toolchain.java.DefaultJavaToolChain}.
 */
public class DefaultCasperjsToolchain extends DefaultToolchain implements CasperjsToolchain {

    protected DefaultCasperjsToolchain(final ToolchainModel model, final Logger logger) {
        super(model, KEY_CASPERJS_TYPE, logger);
    }

    private String casperjsExecutable;

    @Override
    public String findTool(final String toolName) {
        if (KEY_CASPERJS_TYPE.equals(toolName)) {
            final File casperjs = new File(FileUtils.normalize(getCasperjsExecutable()));
            if (casperjs.exists()) {
                return casperjs.getAbsolutePath();
            }
        }
        return null;
    }

    @Override
    public String getCasperjsExecutable() {
        return this.casperjsExecutable;
    }

    @Override
    public void setCasperjsExecutable(final String casperjsExecutable) {
        this.casperjsExecutable = casperjsExecutable;
    }

    @Override
    public String toString() {
        return "CASPERJS[" + getCasperjsExecutable() + "]";
    }
}
