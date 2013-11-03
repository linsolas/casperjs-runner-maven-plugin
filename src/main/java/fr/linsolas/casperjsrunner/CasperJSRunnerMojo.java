package fr.linsolas.casperjsrunner;

import static fr.linsolas.casperjsrunner.LogUtils.getLogger;
import static fr.linsolas.casperjsrunner.PatternsChecker.checkPatterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Runs JavaScript and/or CoffeScript test files on CasperJS instance
 * User: Romain Linsolas
 * Date: 09/04/13
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, threadSafe = true)
public class CasperJSRunnerMojo extends AbstractMojo {

    // Parameters for the plugin

    @Parameter(property = "casperjs.executable", defaultValue = "casperjs")
    private String casperExec;

    @Parameter(property = "casperjs.tests.directory", defaultValue = "${basedir}/src/test/js")
    private File testsDir;

    @Parameter(property = "casperjs.test")
    private String test;

    @Parameter
    private List<String> testsPatterns;

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

    private DefaultArtifactVersion casperJsVersion;

    private void init() throws MojoFailureException {
        LogUtils.setLog(getLog(), verbose);
        if (StringUtils.isBlank(casperExec)) {
            throw new MojoFailureException("CasperJS executable is not defined");
        }
        // Test CasperJS
        casperJsVersion = new DefaultArtifactVersion(checkVersion(casperExec));
        if (verbose) {
            getLogger().info("CasperJS version: " + casperJsVersion);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        init();
        List<String> scripts = new ScriptsFinder(testsDir, test, checkPatterns(testsPatterns, includeJS, includeCS)).findScripts();
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
        CommandLine cmdLine = new CommandLine(casperExec);
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

    private String checkVersion(String casperExecutable) throws MojoFailureException {
        getLogger().debug("Check CasperJS version");
        InputStream stream = null;
        try {
            Process child = Runtime.getRuntime().exec(casperExecutable + " --version");
            stream = child.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String version = reader.readLine();
            return version;
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
        getLogger().debug("Execute CasperJS command [" + line + "]");
        try {
            return new DefaultExecutor().execute(line);
        } catch (final IOException e) {
            if (verbose) {
                getLogger().error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }

}
