package com.github.linsolas.casperjsrunner;

import static com.github.linsolas.casperjsrunner.LogUtils.getLogger;
import static com.github.linsolas.casperjsrunner.PatternsChecker.checkPatterns;
import static com.google.common.collect.Sets.newTreeSet;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
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

import com.github.linsolas.casperjsrunner.toolchain.DefaultCasperjsToolchain;

/**
 * Runs JavaScript and/or CoffeScript test files on CasperJS instance
 * @author Romain Linsolas
 * @since 09/04/13
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, threadSafe = true)
public class CasperJSRunnerMojo extends AbstractMojo {

    // Parameters for the plugin

    /**
     * Complete path of the executable for CasperJS.
     * <br/><b>Default value:</b>
     * Found from <a href="http://maven.apache.org/guides/mini/guide-using-toolchains.html">toolchain</a> named <b><i>casperjs</b></i>,
     * then from this parameter,
     * then from PATH with default value of <b>casperjs</b>
     */
    @Parameter(property = "casperjs.executable")
    private String casperExecPath;

    /**
     * Directory where the tests to execute are stored.
     * <br/>If <code>${tests.directory}/includes</code> and <code>${tests.directory}/scripts</code> directories exist,
     * this is changed to <code>${tests.directory}/scripts</code> and all <code>*.js</code> files in <code>${tests.directory}/includes</code>
     * will automatically be added to the CasperJS <code>--includes</code> list.
     */
    @Parameter(property = "casperjs.tests.directory", defaultValue = "${basedir}/src/test/casperjs")
    private File testsDir;

    /**
     * Specify this parameter to run individual tests by file name, overriding the <code>testIncludes</code>/<code>testExcludes</code> parameters.
     * Each pattern you specify here will be used to create an include pattern formatted like <code>**&#47;${test}.{js,coffee}</code>, so you can
     * just type "-Dtest=MyTest" to run a single test called <code>foo/MyTest.js</code> or <code>foo/MyTest.coffee</code>.
     */
    @Parameter(property = "casperjs.test")
    private String test;

    /**
     * A list of <code>&lt;testsInclude&gt;</code> elements specifying the tests (by pattern) that should be included in testing.
     * <br/><b>Default value:</b> When not specified and when the test parameter is not specified, the default includes will be
     * (javascript patterns will only be set if <code>includeJS</code> is <code>true</code>, and coffee patterns will only be set
     * if <code>includeCS</code> is <code>true</code>)
<br/><br/>
<code>&lt;testsIncludes&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;Test*.js&lt;/testsInclude&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;*Test.js&lt;/testsInclude&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;*TestCase.js&lt;/testsInclude&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;Test*.coffee&lt;/testsInclude&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;*Test.coffee&lt;/testsInclude&gt;<br/>
&nbsp;&nbsp;&lt;testsInclude&gt;**&#47;*TestCase.coffee&lt;/testsInclude&gt;<br/>
&lt;/testsIncludes&gt;</code>
     */
    @Parameter
    private List<String> testsIncludes;

    /**
     * A list of <code>&lt;testsExclude&gt;</code> elements specifying the tests (by pattern) that should be excluded in testing.
     */
    @Parameter
    private List<String> testsExcludes;

    /**
     * Do we ignore the tests failures. If yes, the plugin will not fail at the end if there was tests failures.
     */
    @Parameter(property = "casperjs.ignoreTestFailures", defaultValue = "${maven.test.failure.ignore}")
    private boolean ignoreTestFailures = false;

    /**
     * Set the plugin to be verbose during its execution. It will ALSO impact the verbosity of the CasperJS execution (ie, setting
     * the --verbose command line option).
     */
    @Parameter(property = "casperjs.verbose", defaultValue = "${maven.verbose}")
    private boolean verbose = false;

    /**
     * A flag to indicate if the *.js found in <code>tests.directory</code> should be executed.
     */
    @Parameter(property = "casperjs.include.javascript", defaultValue="true")
    private boolean includeJS;

    /**
     * A flag to indicate if the *.coffee found in <code>tests.directory</code> should be executed.
     */
    @Parameter(property = "casperjs.include.coffeescript", defaultValue="true")
    private boolean includeCS;

    /**
     * Environment variables to set on the command line, instead of the default, inherited, ones.
     */
    @Parameter
    private Map<String, String> environmentVariables;

    /**
     * Set this to <code>true</code> to bypass unit tests entirely.
     */
    @Parameter(property = "casperjs.skip", defaultValue="${maven.test.skip}")
    private boolean skip = false;

    // Parameters for the CasperJS options

    /**
     * Set the value for the CasperJS option <code>--pre=[pre-test.js]</code>: will add the tests contained in pre-test.js
     * before executing the test suite. If a <code>pre.js</code> file is found on the <code>${tests.directory}</code>, this
     * option will be set automatically
     */
    @Parameter(property = "casperjs.pre")
    private String pre;

    /**
     * Set the value for the CasperJS option <code>--post=[post-test.js]</code>: will add the tests contained in post-test.js
     * after having executed the whole test suite. If a <code>post.js</code> file is found on the <code>${tests.directory}</code>,
     * this option will be set automatically
     */
    @Parameter(property = "casperjs.post")
    private String post;

    /**
     * Set the value for the CasperJS option <code>--includes=[foo.js,bar.js]</code>: will includes the foo.js and bar.js files
     * before each test file execution.
     */
    @Parameter(property = "casperjs.includes")
    private String includes;

    /**
     * A list of <code>&lt;includesPattern&gt;</code> elements specifying the files (by pattern) to set on the <code>--includes</code>
     * option.<br/>When not specified and the <code>${tests.directory}/includes</code> directory exists, this will be set to 
<br/><br/>
<code>&lt;includesPatterns&gt;<br/>
&nbsp;&nbsp;&lt;includesPattern&gt;${tests.directory}/includes/**&#47;*.js&lt;/includesPattern&gt;<br/>
&lt;/includesPatterns&gt;</code>
     */
    @Parameter
    private List<String> includesPatterns;

    /**
     * Should CasperJS generates XML report, through the <code>--xunit=[filename]</code> option.
     * If <code>true</code>, such a report will be generated in the <code>${project.build.directory}/casperjs/<code> directory,
     * with a name of <code>TEST-&lt;test filename&gt;.xml</code>.
     */
    @Parameter(property = "casperjs.enableXmlReport", defaultValue = "false")
    private boolean enableXmlReport;

    @Parameter(property = "casperjs.generateLogs", defaultValue = "false")
    private boolean generateLogs;

    @Parameter(property = "casperjs.generateCookies", defaultValue = "false")
    private boolean generateCookies;

    @Parameter(property = "casperjs.cookies")
    private String cookies;

    /**
     * Set the value for the CasperJS option <code>--log-level=[logLevel]</code>: sets the logging level (see http://casperjs.org/logging.html).
     */
    @Parameter(property = "casperjs.logLevel")
    private String logLevel;

    /**
     * Set the value for the CasperJS option --direct: will output log messages directly to the console.
     */
    @Deprecated
    @Parameter(property = "casperjs.direct", defaultValue = "false")
    private boolean direct;

    /**
     * Set the value for the CasperJS option --verbose: will output log messages directly to the console.
     */
    @Parameter(property = "casperjs.casper.verbose", defaultValue = "false")
    private boolean casperVerbose;

    /**
     * Set the value for the CasperJS option --fail-fast: will terminate the current test suite as soon as a first failure is encountered.
     */
    @Parameter(property = "casperjs.failFast", defaultValue = "false")
    private boolean failFast;

    /**
     * CasperJS 1.1 and above<br/>Set the for the CasperJS option <code>--engine=[engine]</code>: will change the rendering engine
     * (phantomjs or slimerjs)
     */

    @Parameter(property = "casperjs.ignoreSslErrors", defaultValue = "false")
    private boolean ignoreSslErrors;

    @Parameter(property = "casperjs.proxyAuth")
    private String proxyAuth;

    @Parameter(property = "casperjs.engine")
    private String engine;

    /**
     * A list of <code>&lt;argument&gt;</code> to add to the casperjs command line.
     */
    @Parameter
    private List<String> arguments;

    // Injected components

    /**
     * The directory where output files (like xUnit reports) will be stored
     */
    @Parameter(defaultValue="${project.build.directory}/casperjs")
    private File targetDir;

    /**
     * The current maven session, used by the ToolChainManager
     */
    @Parameter(defaultValue="${session}")
    private MavenSession session;

    /**
     * ToolChainManager, used to retrieve the CasperJS runtime path from user's configured toolchains
     */
    @Component
    private ToolchainManager toolchainManager;

    /**
     * The CasperJS runtime path that we will launch
     */
    private String casperRuntime;

    /**
     * The CasperJS runtime version
     */
    private DefaultArtifactVersion casperJsVersion;

    /**
     * The directory containing the scripts to include while launching tests
     */
    private File includesDir;

    /**
     * The directory containing the tests to launch
     */
    private File scriptsDir;

    private boolean mustCopyCookies = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LogUtils.setLog(getLog(), verbose);
        if (skip) {
            getLogger().info("Skipping CasperJsRunner execution");
            return;
        }
        init();
        TreeSet<String> scripts = findScripts();
        Result globalResult = executeScripts(scripts);
        getLogger().info(globalResult.print());
        if (!ignoreTestFailures && globalResult.getFailures() > 0) {
            throw new MojoFailureException("There are " + globalResult.getFailures() + " tests failures");
        }
    }

    private void init() throws MojoFailureException {
        findCasperRuntime();
        if (StringUtils.isBlank(casperRuntime)) {
            throw new MojoFailureException("CasperJS executable not found");
        }

        retrieveVersion();
        if (verbose) {
            getLogger().info("CasperJS version: " + casperJsVersion);
        }

        testsIncludes = checkPatterns(testsIncludes, includeJS, includeCS);

        if (testsExcludes == null) {
            testsExcludes = new ArrayList<String>();
        }

        if (includesPatterns == null) {
            includesPatterns = new ArrayList<String>();
        }

        includesDir = testsDir;
        scriptsDir = testsDir;
        File defaultIncludesDir = new File(testsDir, "includes");
        File defaultScriptsDir = new File(testsDir, "scripts");
        if (defaultScriptsDir.exists() && defaultScriptsDir.isDirectory()) {
            getLogger().debug("'scripts' subdirectory found, altering 'scriptsDir'");
            scriptsDir = defaultScriptsDir;
            if (defaultIncludesDir.exists() && defaultIncludesDir.isDirectory() && includesPatterns.isEmpty()) {
                getLogger().debug("'includes' subdirectory found and 'includesPatterns' is empty, altering 'includesDir' and 'includesPatterns'");
                includesDir = defaultIncludesDir;
                includesPatterns.add("**/*.js");
            }
        }

    }

    private TreeSet<String> findScripts() {
        return newTreeSet(new ScriptsFinder(scriptsDir, test, testsIncludes, testsExcludes).findScripts());
    }

    private Result executeScripts(final TreeSet<String> files) {
        Result result = new Result();
        for (String file : files) {
            File f = new File(scriptsDir, file);
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

        // Option --verbose
        if (verbose) {
            cmdLine.addArgument("--verbose");
        }
        // Option --log-level, to set the log level
        if (StringUtils.isNotBlank(logLevel)) {
            cmdLine.addArgument("--log-level=" + logLevel);
        }

        cmdLine.addArgument("test");

        // Option --includes, to includes files before each test execution
        if (StringUtils.isNotBlank(includes)) {
            cmdLine.addArgument("--includes=" + includes);
        } else if (!includesPatterns.isEmpty()) {
            List<String> incs = new IncludesFinder(includesDir, includesPatterns).findIncludes();
            if (incs != null && !incs.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("--includes=");
                for (String inc : incs) {
                    builder.append(new File(includesDir, inc).getAbsolutePath());
                    builder.append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                cmdLine.addArgument(builder.toString());
            }
        }
        // Option --pre, to execute the scripts before the test suite
        if (StringUtils.isNotBlank(pre)) {
            cmdLine.addArgument("--pre=" + pre);
        } else if (new File(testsDir, "pre.js").exists()) {
            getLogger().debug("Using automatically found 'pre.js' file on " + testsDir.getName() + " directory as --pre");
            cmdLine.addArgument("--pre=" + new File(testsDir, "pre.js").getAbsolutePath());
        }
        // Option --post, to execute the scripts after the test suite
        if (StringUtils.isNotBlank(post)) {
            cmdLine.addArgument("--post=" + post);
        } else if (new File(testsDir, "post.js").exists()) {
            getLogger().debug("Using automatically found 'post.js' file on " + testsDir.getName() + " directory as --post");
            cmdLine.addArgument("--post=" + new File(testsDir, "post.js").getAbsolutePath());
        }
        // Option --xunit, to export results in XML file
        if (enableXmlReport) {
            cmdLine.addArgument("--xunit=results.xml");
        }

        if (StringUtils.isNotBlank(cookies)) {
            File cookFile = new File(cookies);
            if(cookFile.isFile()) {
                cmdLine.addArgument("--cookies-file=" + cookies);
                if(generateCookies) {
                    mustCopyCookies = true;
                }
            } else {
                getLogger().warn("Couldn't find the specified cookies file: \"" + cookies + "\".");
            }
        } else if (generateCookies) {
            cmdLine.addArgument("--cookies-file=cookies_file.txt");
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
        // Option --verbose, to output log messages to the console
        if (casperVerbose) {
            cmdLine.addArgument("--verbose");
        }
        // Option --ignore-ssl-errors
        if(ignoreSslErrors) {
            cmdLine.addArgument("--ignore-ssl-errors=true");
        }
        // Option --proxy-auth
        if(StringUtils.isNotBlank(proxyAuth)) {
            cmdLine.addArgument("--proxy-auth=" + proxyAuth);
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
        return executeCommand(cmdLine, f);
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
            getLogger().debug("No toolchain found, falling back to parameter");
            casperRuntime = casperExecPath;
        }

        if (casperRuntime == null) {
            getLogger().debug("No parameter specified, falling back to default 'casperjs'");
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

    private int executeCommand(CommandLine line, File f) {
        getLogger().debug("Execute CasperJS command [" + line + "], with env: " + environmentVariables);
        try {
            DefaultExecutor executor = new DefaultExecutor();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
            File scriptOutputDir = new File(getScriptOutputDir(f));
            int cmdReturnedValue;
            scriptOutputDir.mkdirs();
            executor.setExitValues(new int[] {0, 1});
            executor.setStreamHandler(streamHandler);
            executor.setWorkingDirectory(scriptOutputDir);
            cmdReturnedValue = executor.execute(line);
            getLogger().info("CasperJS console output for '" + f.getName() + "'\n" + outputStream.toString());

            if(generateLogs) {
                generateLog(line, scriptOutputDir.getAbsolutePath(), outputStream.toString());
            }

            if(mustCopyCookies) {
                new FileUtils().copyFile(new File(cookies), new File(scriptOutputDir.getAbsolutePath() + "\\cookies_file.txt"));
            }

            return cmdReturnedValue;
        } catch (final IOException e) {
            if (verbose) {
                getLogger().error("Could not run CasperJS command", e);
            }
            return -1;
        }
    }

    private void generateLog(CommandLine line, String scriptOutputDir, String consoleOutput) {
        try {
            File log = new File(scriptOutputDir, "build.log");
            String[] lineTmp;
            ArrayList<String> lineResult = new ArrayList<String>();

            log.createNewFile();

            lineTmp = line.toString().split(", ");
            lineTmp[1] = lineTmp[0] + " " + lineTmp[1];

            for(int i = 1; i < lineTmp.length; i++) {
                lineResult.add(lineTmp[i]);
            }
            lineResult.add("");
            lineResult.add(consoleOutput);

            new FileUtils().writeLines(log, lineResult);
        } catch (final IOException e) {
            getLogger().error("Could not create the log file of the script: ", e);
        }
    }

    private String getScriptOutputDir(File script) {
        String[] outputPath;
        String scriptOutputDir = "", tmpFileName = "";
        File tmpFile;
        int i, testsDirDepthFromRoot = -1;

        outputPath = script.getAbsolutePath().split("\\\\|/");

        i = 0;
        while(testsDirDepthFromRoot == -1) {
            tmpFileName += outputPath[i] + File.separatorChar;
            tmpFile = new File(tmpFileName);
            if(tmpFile.getAbsolutePath().equals(testsDir.getAbsolutePath())) {
                testsDirDepthFromRoot = i;
            } else {
                i++;
            }
        }

        for(i = testsDirDepthFromRoot; i < outputPath.length - 1; i++) {
            scriptOutputDir += outputPath[i] + File.separatorChar;
        }
        scriptOutputDir += script.getName().replaceAll("\\.", "_");

        return targetDir.getAbsolutePath() + File.separatorChar + scriptOutputDir;
    }
}
