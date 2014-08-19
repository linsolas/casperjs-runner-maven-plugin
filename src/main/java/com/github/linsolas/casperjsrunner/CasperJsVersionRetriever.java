package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.LogUtils.getLogger;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.MojoFailureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CasperJsVersionRetriever {

    public static DefaultArtifactVersion retrieveVersion(final String casperRuntime, final boolean verbose)
            throws MojoFailureException {
        getLogger().debug("Check CasperJS version");
        InputStream stream = null;
        try {
            final Process child = Runtime.getRuntime().exec(casperRuntime + " --version");
            stream = child.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            final String version = reader.readLine();
            return new DefaultArtifactVersion(version);
        } catch (final IOException e) {
            if (verbose) {
                getLogger().error("Could not run CasperJS command", e);
            }
            throw new MojoFailureException("Unable to determine casperJS version");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final IOException e) {
                }
            }
        }
    }

}
