package fr.linsolas.casperjsrunner.toolchain;

import org.apache.maven.toolchain.DefaultToolchain;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;

/**
 * Based on {@code org.apache.maven.toolchain.java.DefaultJavaToolChain}.
 */
public class DefaultCasperjsToolchain extends DefaultToolchain implements CasperjsToolchain {

    public static final String KEY_CASPERJS_TYPE = "casperjs";
    public static final String KEY_CASPERJS_EXECUTABLE = "casperjsExecutable";

    protected DefaultCasperjsToolchain(ToolchainModel model, Logger logger) {
        super(model, KEY_CASPERJS_TYPE, logger);
    }

    private String casperjsExecutable;

    public String findTool(String toolName) {
        if (KEY_CASPERJS_TYPE.equals(toolName)) {
            File casperjs = new File(FileUtils.normalize(getCasperjsExecutable()));
            if (casperjs.exists()) {
                return casperjs.getAbsolutePath();
            }
        }
        return null;
    }

    public String getCasperjsExecutable() {
        return this.casperjsExecutable;
    }

    public void setCasperjsExecutable(String casperjsExecutable) {
        this.casperjsExecutable = casperjsExecutable;
    }

    public String toString() {
        return "CASPERJS[" + getCasperjsExecutable() + "]";
    }
}
