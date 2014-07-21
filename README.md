casperjs-runner-maven-plugin
============================

This project aims to run [CasperJS](http://casperjs.org/) tests in a Maven build.

## System requirements

This plugin has been tested on two environments :
- PhantomJS (v **1.8.2**) and CasperJS (v **1.0.4**) where installed.
- PhantomJS (v **1.9.7**) and CasperJS (v **1.1.0-beta3**) where installed.


## Build

Download the sources, and build the plugin using the ```mvn clean install``` command. You can build this plugin using Maven 2.2.x or Maven 3.x, and a JDK 1.6.

## Usage

Add, in your ```<build><plugins>``` part of your ```pom.xml``` file the following part:

```xml
    <plugin>
        <groupId>fr.linsolas</groupId>
        <artifactId>casperjs-runner-maven-plugin</artifactId>
        <version>1.1-RC1</version>
        <configuration>
            <!-- see below -->
        </configuration>
    </plugin>
```

## Configuration


The CasperJS Runner Maven plugin can be configured with the following options:

<table>
    <tr>
        <th>Name</th>
        <th>Description</th>
        <th>Mandatory</th>
        <th>User property (to set from command line)</th>
    </tr>
    <tr>
        <td><code>casperExecPath</code></td>
        <td>Complete path of the executable for CasperJS.
        <br/><b>Default value:</b> Found from <a href="http://maven.apache.org/guides/mini/guide-using-toolchains.html">toolchain</a> named <b><i>casperjs</b></i>, then from this parameter, then from PATH with default value of <b>casperjs</b></td>
        <td>No</td>
        <td><code>casperjs.executable</code></td>
    </tr>
    <tr>
        <td><code>testsDir</code></td>
        <td>Directory where the tests to execute are stored.
        <br/><b>Default value:</b> <code>${basedir}/src/test/casperjs</code>.<br/>
        If <code>${tests.directory}/includes</code> and <code>${tests.directory}/scripts</code> directories exist, this is changed to <code>${tests.directory}/scripts</code> and all <code>*.js</code> files in <code>${tests.directory}/includes</code> will automatically be added to the CasperJS <code>--includes</code> list.</td>
        <td>No</td>
        <td><code>tests.directory</code></td>
    </tr>
    <tr>
        <td><code>test</code></td>
        <td>Specify this parameter to run individual tests by file name, overriding the <code>testIncludes</code>/<code>testExcludes</code> parameters. Each pattern you specify here will be used to create an include pattern formatted like <code>**/${test}.{js,coffee}</code>, so you can just type "-Dtest=MyTest" to run a single test called <code>foo/MyTest.js</code> or <code>foo/MyTest.coffee</code>.
        <br/><b>Default value:</b> none.</td>
        <td>No</td>
        <td><code>casperjs.test</code></td>
    </tr>
    <tr>
        <td><code>testIncludes</code></td>
        <td>A list of <code>&lt;testInclude&gt;</code> elements specifying the tests (by pattern) that should be included in testing.
        <br/><b>Default value:</b> When not specified and when the test parameter is not specified, the default includes will be (javascript patterns will only be set if <code>includeJS</code> is <code>true</code>, and coffee patterns will only be set if <code>includeCS</code> is <code>true</code>)
<br/><br/>
<code>&lt;testIncludes&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/Test*.js&lt;/testInclude&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/*Test.js&lt;/testInclude&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/*TestCase.js&lt;/testInclude&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/Test*.coffee&lt;/testInclude&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/*Test.coffee&lt;/testInclude&gt;<br/>
&nbsp;&nbsp;&lt;testInclude&gt;**/*TestCase.coffee&lt;/testInclude&gt;<br/>
&lt;/testIncludes&gt;</code></td>
        <td>No</td>
        <td></td>
    </tr>
    <tr>
        <td><code>testExcludes</code></td>
        <td>A list of <code>&lt;testExclude&gt;</code> elements specifying the tests (by pattern) that should be excluded in testing.
        <br/><b>Default value:</b> none.</td>
        <td>No</td>
        <td></td>
    </tr>
    <tr>
        <td><code>ignoreTestFailures</code></td>
        <td>Do we ignore the tests failures. If yes, the plugin will not fail at the end if there was tests failures.
        <br/><b>Default value:</b> <code>${maven.test.failure.ignore}</code>, falling back to false</td>
        <td>No</td>
        <td><code>casperjs.ignoreTestFailures</code></td>
    </tr>
    <tr>
        <td><code>includeJS</code></td>
        <td>A flag to indicate if the *.js found in <code>tests.directory</code> should be executed.
        <br/><b>Default value:</b> true</td>
        <td>No</td>
        <td><code>casperjs.include.javascript</code></td>
    </tr>
    <tr>
        <td><code>includeCS</code></td>
        <td>A flag to indicate if the *.coffee found in <code>tests.directory</code> should be executed.
        <br/><b>Default value:</b> true</td>
        <td>No</td>
        <td><code>casperjs.include.coffeescript</code></td>
    </tr>
    <tr>
        <td><code>environmentVariables</code></td>
        <td>Environment variables to set on the command line, instead of the default, inherited, ones.
        <br/><b>Default value:</b> none.</td>
        <td>No</td>
        <td></td>
    </tr>
    <tr>
        <td><code>skip</code></td>
        <td>Set this to <code>true</code> to bypass unit tests entirely.
        <br/><b>Default value:</b> <code>${maven.test.skip}</code>, falling back to false</td>
        <td>No</td>
        <td><code>casperjs.skip</code></td>
    </tr>
    <tr>
        <td><code>verbose</code></td>
        <td>Set the plugin to be verbose during its execution. It will ALSO impact the verbosity of the CasperJS execution (ie, setting the --verbose command line option).
        <br/><b>Default value:</b> <code>${maven.verbose}</code>, falling back to false</td>
        <td>No</td>
        <td><code>casperjs.verbose</code></td>
    </tr>
</table>

You can also add in the ```<configuration>``` part several elements that will be set as [CasperJ options](http://casperjs.org/testing.html):

<table>
    <tr>
        <th>Name</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>pre</code></td>
        <td>Set the value for the CasperJS option <code>--pre=[pre-test.js]</code>: will add the tests contained in pre-test.js before executing the test suite.
        If a <code>pre.js</code> file is found on the <code>${tests.directory}</code>, this option will be set automatically</td>
    </tr>
    <tr>
        <td><code>post</code></td>
        <td>Set the value for the CasperJS option <code>--post=[post-test.js]</code>: will add the tests contained in post-test.js after having executed the whole test suite.
        If a <code>post.js</code> file is found on the <code>${tests.directory}</code>, this option will be set automatically</td>
    </tr>
    <tr>
        <td><code>includes</code></td>
        <td>Set the value for the CasperJS option <code>--includes=[foo.js,bar.js]</code>: will includes the foo.js and bar.js files before each test file execution.</td>
    </tr>
    <tr>
        <td><code>includesPatterns</code></td>
        <td>A list of <code>&lt;includesPattern&gt;</code> elements specifying the files (by pattern) to set on the <code>--includes</code> option.<br/>
        When not specified and the <code>${tests.directory}/includes</code> directory exists, this will be set to 
<br/><br/>
<code>&lt;includesPatterns&gt;<br/>
&nbsp;&nbsp;&lt;includesPattern&gt;${tests.directory}/includes/**/*.js&lt;/includesPattern&gt;<br/>
&lt;/includesPatterns&gt;</code></td>
    </tr>
    <tr>
        <td><code>xunit</code></td>
        <td>Set the value for the CasperJS option <code>--xunit=[filename]</code>: will export test suite results in the specified xUnit XML file.</td>
    </tr>
    <tr>
        <td><code>logLevel</code></td>
        <td>Set the value for the CasperJS option <code>--log-level=[logLevel]</code>: sets the logging level (see http://casperjs.org/logging.html).</td>
    </tr>
    <tr>
        <td><code>direct</code></td>
        <td>Set the value for the CasperJS option --direct: will output log messages directly to the console.</td>
    </tr>
    <tr>
        <td><code>failFast</code></td>
        <td>Set the value for the CasperJS option --fail-fast: will terminate the current test suite as soon as a first failure is encountered.</td>
    </tr>
    <tr>
        <td><code>engine</code></td>
        <td>CasperJS 1.1 and above<br/>Set the for the CasperJS option <code>--engine=[engine]</code>: will change the rendering engine (phantomjs or slimerjs)</td>
    </tr>
    <tr>
        <td><code>arguments</code></td>
        <td>A list of <code>&lt;argument&gt;</code> to add to the casperjs command line.</td>
    </tr>
</table>


## TODO

Here is a list of things that should be done to make this plugin awesome:

- ~~Set default values for some parameters (```tests.directory``` for example)~~
- ~~Manage sub-directories where files are included~~
- ~~Manage a list of excludes / includes for the tests to run~~
- Publish to Maven Central

## Issues / Enhancements

If you encounter issues or think about any kind of enhancements, you can add them in the [adequate section of the project](https://github.com/linsolas/casperjs-runner-maven-plugin/issues). Do not hesitate also to make Push Requests if you want to enhance this project.

## License

This plugin is licensed with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
