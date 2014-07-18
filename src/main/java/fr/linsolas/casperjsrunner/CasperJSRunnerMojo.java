package fr.linsolas.casperjsrunner;

import static fr.linsolas.casperjsrunner.LogUtils.getLogger;
import static fr.linsolas.casperjsrunner.PatternsChecker.checkPatterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;

import fr.linsolas.casperjsrunner.toolchain.DefaultCasperjsToolchain;

/**
 * Runs JavaScript and/or CoffeScript test files on CasperJS instance
 * User: Romain Linsolas
 * Date: 09/04/13
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, threadSafe = true)
public class CasperJSRunnerMojo extends AbstractMojo {

    // Parameters for the plugin

    @Parameter(property = "casperjs.executable")
    private String casperExecPath;

    @Parameter(property = "casperjs.tests.directory", defaultValue = "${basedir}/src/test/js")
    private File testsDir;

    @Parameter(property = "casperjs.test")
    private String test;

    @Parameter
    private List<String> testsIncludes;

    @Parameter
    private List<String> testsExcludes;

    @Parameter(property = "casperjs.ignoreTestFailures", defaultValue = "${maven.test.failure.ignore}")
    private boolean ignoreTestFailures = false;

    @Parameter(property = "casperjs.verbose", defaultValue = "${maven.verbose}")
    private boolean verbose = false;

    @Parameter
    private List<String> includesPatterns;

    // Parameters for the CasperJS options

    @Parameter(property = "casperjs.include.javascript")
    private boolean includeJS = true;

    @Parameter(property = "casperjs.include.coffeescript")
    private boolean includeCS = true;

    @Parameter(property = "casperjs.pre")
    private String pre;

    @Parameter(property = "casperjs.post")
    private String post;

    @Parameter(property = "casperjs.includes")
    private String includes;

    @Parameter(property = "casperjs.xunit")
    private String xUnit;

    @Parameter(property = "casperjs.logLevel")
    private String logLevel;

    @Parameter(property = "casperjs.direct")
    private boolean direct = false;

    @Parameter(property = "casperjs.failFast")
    private boolean failFast = false;

    @Parameter(property = "casperjs.engine")
    private String engine;

    @Parameter
    private List<String> arguments;

    @Parameter
    private Map<String, String> environmentVariables;

    @Parameter
    private boolean skip = false;

    // Injected components

    @Parameter(defaultValue="${session}")
    private MavenSession session;

    @Component
    private ToolchainManager toolchainManager;

    private String casperRuntime;

    private DefaultArtifactVersion casperJsVersion;

    private void init() throws MojoFailureException {
        findCasperRuntime();
        if (StringUtils.isBlank(casperRuntime)) {
            throw new MojoFailureException("CasperJS executable not found");
        }

        retrieveVersion();
        if (casperJsVersion.getMajorVersion() != 1 || casperJsVersion.getMinorVersion() < 1) {
            throw new MojoFailureException("This version of the plugin only supports CasperJS 1.1+ executable (was "+casperJsVersion+")");
        }
        
        if (verbose) {
            getLogger().info("CasperJS version: " + casperJsVersion);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtils.setLog(getLog(), verbose);
        if (skip) {
            getLogger().info("Skipping CasperJsRunner execution");
            return;
        }
        init();
        List<String> scripts = new ScriptsFinder(testsDir, test, checkPatterns(testsIncludes, includeJS, includeCS), testsExcludes != null ? testsExcludes : new ArrayList<String>()).findScripts();
        Result globalResult = executeScripts(scripts);
        getLogger().info(globalResult.print());
        if (!ignoreTestFailures && globalResult.getFailures() > 0) {
            throw new MojoFailureException("There are " + globalResult.getFailures() + " tests failures");
        }
    }

    private Result executeScripts(final List<String> files) {
        Result result = new Result();
        for (String file : files) {
            File f = new File(testsDir, file);
            getLogger().debug("Execution of test " + f.getName());
            int res = executeScript(f);
            if (res == 0) {
                result.addSuccess();
            } else {
                getLogger().warn("Test '" + f.getName() + "' has failure");
                result.addFailure();
            }
        }
        return result;
    }

    private int executeScript(File f) {
        CommandLine cmdLine = new CommandLine(casperRuntime);
        cmdLine.addArgument("test");

        // Option --includes, to includes files before each test execution
        if (StringUtils.isNotBlank(includes)) {
            cmdLine.addArgument("--includes=" + includes);
        } else if (includesPatterns != null && !includesPatterns.isEmpty()) {
            List<String> incs = new IncludesFinder(testsDir, includesPatterns).findIncludes();
            if (incs != null && !incs.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("--includes=");
                for (String inc : incs) {
                    builder.append(new File(testsDir, inc).getAbsolutePath());
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                cmdLine.addArgument(builder.toString());
            }
        }
        // Option --pre, to execute the scripts before the test suite
        if (StringUtils.isNotBlank(pre)) {
            cmdLine.addArgument("--pre=" + pre);
        }
        // Option --pre, to execute the scripts after the test suite
        if (StringUtils.isNotBlank(post)) {
            cmdLine.addArgument("--post=" + post);
        }
        // Option --xunit, to export results in XML file
        if (StringUtils.isNotBlank(xUnit)) {
            cmdLine.addArgument("--xunit=" + xUnit);
        }
        // Option --fast-fast, to terminate the test suite once a failure is
        // found
        if (failFast) {
            cmdLine.addArgument("--fail-fast");
        }
        // Option --direct, to output log messages to the console
        if (direct) {
            cmdLine.addArgument("--direct");
        }
        // Option --engine, to select phantomJS or slimerJS engine
        if (StringUtils.isNotBlank(engine)) {
            cmdLine.addArgument("--engine=" + engine);
        }
        cmdLine.addArgument(f.getAbsolutePath());
        if (arguments != null && !arguments.isEmpty()) {
            for (String argument : arguments) {
                cmdLine.addArgument(argument, false);
            }
        }
        return executeCommand(cmdLine);
    }

    private void findCasperRuntime() {
        getLogger().debug("Finding casperjs runtime ...");

        getLogger().debug("Trying from toolchain");
        final Toolchain tc = toolchainManager.getToolchainFromBuildContext(DefaultCasperjsToolchain.KEY_CASPERJS_TYPE, session);
        if (tc != null) {
            getLogger().debug("Toolchain in casperjs-plugin: " + tc);
            if (casperExecPath != null) {
                getLogger().warn(
                        "Toolchains are ignored, 'casperRuntime' parameter is set to " + casperExecPath);
                casperRuntime = casperExecPath;
            } else {
                getLogger().debug("Found from toolchain");
                casperRuntime = tc.findTool("casperjs");
            }
        }

        if (casperRuntime == null) {
            getLogger().debug("No toolchain found, failling back to parameter");
            casperRuntime = casperExecPath;
        }

        if (casperRuntime == null) {
            getLogger().debug("No parameter specified, failling back to default 'casperjs'");
            casperRuntime = "casperjs";
        }
    }

    private void retrieveVersion() throws MojoFailureException {
        getLogger().debug("Check CasperJS version");
        InputStream stream = null;
        try {
            Process child = Runtime.getRuntime().exec(casperRuntime + " --version");
            stream = child.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String version = reader.readLine();
            casperJsVersion = new DefaultArtifactVersion(version);
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

    private int executeCommand(CommandLine line) {
        getLogger().debug("Execute CasperJS command [" + line + "], with env: " + environmentVariables);
        try {
            return new DefaultExecutor().execute(line, environmentVariables);
        } catch (final IOException e) {
            if (verbose) {
                getLogger().error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }

}
