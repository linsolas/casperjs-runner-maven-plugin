package fr.linsolas.casperjsrunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
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
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, threadSafe=true)
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

    private Log log = getLog();

    private void init() throws MojoFailureException {
        if (StringUtils.isBlank(casperExec)) {
            throw new MojoFailureException("CasperJS executable is not defined");
        }
        // Test CasperJS
        int res = executeCommand(casperExec + " --version");
        if (res == -1) {
            // Problem
            throw new MojoFailureException("An error occurred when trying to execute CasperJS");
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
        StringBuffer command = new StringBuffer();
        command.append(casperExec);
        // Option --includes, to includes files before each test execution
        if (StringUtils.isNotBlank(includes)) {
            command.append(" --includes=").append(includes);
        }
        // Option --pre, to execute the scripts before the test suite
        if (StringUtils.isNotBlank(pre)) {
            command.append(" --pre=").append(pre);
        }
        // Option --pre, to execute the scripts after the test suite
        if (StringUtils.isNotBlank(post)) {
            command.append(" --post=").append(post);
        }
        // Option --xunit, to export results in XML file
        if (StringUtils.isNotBlank(xUnit)) {
            command.append(" --xunit=").append(xUnit);
        }
        // Option --fast-fast, to terminate the test suite once a failure is found
        if (failFast) {
            command.append(" --fail-fast");
        }
        // Option --direct, to output log messages to the console
        if (direct) {
            command.append(" --direct");
        }
        command.append(' ').append(f.getAbsolutePath());
        return executeCommand(command.toString());
    }

    private int executeCommand(String command) {
        log.debug("Execute CasperJS command [" + command + "]");
        DefaultExecutor exec = new DefaultExecutor();
        CommandLine line = CommandLine.parse(command);
        try {
            return exec.execute(line);
        } catch (IOException e) {
            if (verbose) {
                log.error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }


}

