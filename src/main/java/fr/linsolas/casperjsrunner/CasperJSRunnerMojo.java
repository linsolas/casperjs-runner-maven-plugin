package fr.linsolas.casperjsrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
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

    @Parameter(alias = "casperjs.executable", defaultValue = "casperjs")
    private String casperExec;

    @Parameter(alias = "tests.directory", defaultValue = "${basedir}/src/test/js")
    private File testsDir;

    @Parameter(alias = "ignoreTestFailures")
    private boolean ignoreTestFailures = false;

    @Parameter(alias = "verbose")
    private boolean verbose = false;

    // Parameters for the CasperJS options

    @Parameter(alias = "include.javascript")
    private boolean includeJS = true;

    @Parameter(alias = "include.coffeescript")
    private boolean includeCS = true;

    @Parameter(alias = "pre")
    private String pre;

    @Parameter(alias = "post")
    private String post;

    @Parameter(alias = "includes")
    private String includes;

    @Parameter(alias = "xunit")
    private String xUnit;

    @Parameter(alias = "logLevel")
    private String logLevel;

    @Parameter(alias = "direct")
    private boolean direct = false;

    @Parameter(alias = "failFast")
    private boolean failFast = false;

    @Parameter(alias = "engine")
    private String engine;

    @Parameter
    private List<String> arguments;

    private DefaultArtifactVersion casperJsVersion;

    private Log log = getLog();

    private void init() throws MojoFailureException {
        if (StringUtils.isBlank(casperExec)) {
            throw new MojoFailureException("CasperJS executable is not defined");
        }
        // Test CasperJS
        casperJsVersion = new DefaultArtifactVersion(checkVersion(casperExec));
        if (verbose) {
            log.info("CasperJS version: " + casperJsVersion);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        init();
        Result globalResult = new Result();
        log.info("Looking for scripts in " + testsDir + "...");
        if (includeJS) {
            globalResult.add(executeScripts(".js"));
        } else {
            log.info("JavaScript files ignored");
        }
        if (includeCS) {
            globalResult.add(executeScripts(".coffee"));
        } else {
            log.info("CoffeeScript files ignored");
        }
        log.info(globalResult.print());
        if (!ignoreTestFailures && globalResult.getFailures() > 0) {
            throw new MojoFailureException("There are " + globalResult.getFailures() + " tests failures");
        }
    }

    private Result executeScripts(final String ext) {
        Result result = new Result();
        List<File> files = findFiles(ext, testsDir);
        if (files.isEmpty()) {
            log.warn("No " + ext + " files found in directory " + testsDir);
        } else {
            for (File f : files) {
                log.debug("Execution of test " + f.getName());
                int res = executeScript(f);
                if (res == 0) {
                    result.addSuccess();
                } else {
                    log.warn("Test '" + f.getName() + "' has failure");
                    result.addFailure();
                }
            }
        }
        return result;
    }

    private List<File> findFiles(String ext, File folder) {
        List<File> files = new ArrayList<File>();
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                files.addAll(findFiles(ext, f));
            } else if (StringUtils.endsWithIgnoreCase(f.getName(), ext)) {
                files.add(f);
            }
        }
        return files;
    }

    private int executeScript(File f) {
        CommandLine cmdLine = new CommandLine(casperExec);
        if (casperJsVersion.compareTo(new DefaultArtifactVersion("1.1")) >= 0) {
            cmdLine.addArgument("test");
        }
        // Option --includes, to includes files before each test execution
        if (StringUtils.isNotBlank(includes)) {
            cmdLine.addArgument(" --includes="+includes);
        }
        // Option --pre, to execute the scripts before the test suite
        if (StringUtils.isNotBlank(pre)) {
            cmdLine.addArgument(" --pre="+pre);
        }
        // Option --pre, to execute the scripts after the test suite
        if (StringUtils.isNotBlank(post)) {
            cmdLine.addArgument(" --post="+post);
        }
        // Option --xunit, to export results in XML file
        if (StringUtils.isNotBlank(xUnit)) {
            cmdLine.addArgument(" --xunit="+xUnit);
        }
        // Option --fast-fast, to terminate the test suite once a failure is
        // found
        if (failFast) {
            cmdLine.addArgument(" --fail-fast");
        }
        // Option --direct, to output log messages to the console
        if (direct) {
            cmdLine.addArgument(" --direct");
        }
        // Option --engine, to select phantomJS or slimerJS engine
        if (StringUtils.isNotBlank(engine)) {
            cmdLine.addArgument(" --engine="+engine);
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
        log.debug("Check CasperJS version");
        InputStream stream = null;
        try {
            Process child = Runtime.getRuntime().exec(casperExecutable + " --version");
            stream = child.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String version = reader.readLine();
            return version;
        } catch (final IOException e) {
            if (verbose) {
                log.error("Could not run CasperJS command", e);
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
        log.debug("Execute CasperJS command [" + line + "]");
        try {
            return new DefaultExecutor().execute(line);
        } catch (final IOException e) {
            if (verbose) {
                log.error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }

}
