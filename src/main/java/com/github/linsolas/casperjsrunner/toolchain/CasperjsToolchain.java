package com.github.linsolas.casperjsrunner.toolchain;

import org.apache.maven.toolchain.Toolchain;

/**
 * A tool chain for CasperJS runtime.
 */
public interface CasperjsToolchain extends Toolchain {

    String KEY_CASPERJS_TYPE = "casperjs";

    String KEY_CASPERJS_EXECUTABLE = "casperjsExecutable";

    String getCasperjsExecutable();

    void setCasperjsExecutable(String casperjsExecutable);
}
