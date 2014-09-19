package com.github.linsolas.casperjsrunner.toolchain;

import static com.github.linsolas.casperjsrunner.toolchain.CasperjsToolchain.KEY_CASPERJS_EXECUTABLE;
import static com.github.linsolas.casperjsrunner.toolchain.CasperjsToolchain.KEY_CASPERJS_TYPE;

import org.apache.maven.toolchain.MisconfiguredToolchainException;
import org.apache.maven.toolchain.RequirementMatcherFactory;
import org.apache.maven.toolchain.ToolchainFactory;
import org.apache.maven.toolchain.ToolchainPrivate;
import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;

/**
 * Based on {@code org.apache.maven.toolchain.java.DefaultJavaToolchainFactory}.
 */
@Component(role = ToolchainFactory.class, hint = KEY_CASPERJS_TYPE, description = "A default factory for '"
        + KEY_CASPERJS_TYPE + "' toolchains")
public class DefaultCasperjsToolchainFactory implements ToolchainFactory, LogEnabled {

    private Logger logger;

    @Override
    public ToolchainPrivate createToolchain(final ToolchainModel model)
            throws MisconfiguredToolchainException {
        if (model == null) {
            return null;
        }
        final DefaultCasperjsToolchain toolchain = new DefaultCasperjsToolchain(model, logger);
        Xpp3Dom dom = (Xpp3Dom) model.getConfiguration();
        final Xpp3Dom casperjsExecutable = dom.getChild(KEY_CASPERJS_EXECUTABLE);
        if (casperjsExecutable == null) {
            throw new MisconfiguredToolchainException("CasperJS toolchain without the "
                    + KEY_CASPERJS_EXECUTABLE + " configuration element.");
        }
        final File normal = new File(FileUtils.normalize(casperjsExecutable.getValue()));
        if (normal.exists()) {
            toolchain.setCasperjsExecutable(FileUtils.normalize(casperjsExecutable.getValue()));
        } else {
            throw new MisconfiguredToolchainException("Non-existing casperjs executable at "
                    + normal.getAbsolutePath());
        }

        // now populate the provides section.
        dom = (Xpp3Dom) model.getProvides();
        final Xpp3Dom[] provides = dom.getChildren();
        for (final Xpp3Dom provide : provides) {
            final String key = provide.getName();
            final String value = provide.getValue();
            if (value == null) {
                throw new MisconfiguredToolchainException("Provides token '" + key
                        + "' doesn't have any value configured.");
            }
            if ("version".equals(key)) {
                toolchain.addProvideToken(key, RequirementMatcherFactory.createVersionMatcher(value));
            } else {
                toolchain.addProvideToken(key, RequirementMatcherFactory.createExactMatcher(value));
            }
        }
        return toolchain;
    }

    @Override
    public ToolchainPrivate createDefaultToolchain() {
        return null;
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public void enableLogging(final Logger l) {
        this.logger = l;
    }
}
