package com.github.linsolas.casperjsrunner.toolchain;

import org.apache.maven.toolchain.Toolchain;

/**
 * A tool chain for CasperJS runtime.
 */
public interface CasperjsToolchain extends Toolchain {

    String getCasperjsExecutable();

    void setCasperjsExecutable(String casperjsExecutable);
}
