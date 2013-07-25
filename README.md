casperjs-runner-maven-plugin
============================

This project aims to run [CasperJS](http://casperjs.org/) tests in a Maven build.

## System requirements

This plugin has been tested on an environment where PhantomJS (v**1.9.0**) and CasperJS (v**1.0.2**) where installed.


## Build

Download the sources, and build the plugin using the ```mvn clean install``` command. You can build this plugin using Maven 2.2.x or Maven 3.x, and a JDK 1.6.

## Usage

Add, in your ```<build><plugins>``` part of your ```pom.xml``` file the following part:

```xml
    <plugin>
        <groupId>fr.linsolas</groupId>
        <artifactId>casperjs-runner-maven-plugin</artifactId>
        <version>1.0-RC1</version>
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
        <th>Default value</th>
        <th>Mandatory</th>
    </tr>
    <tr>
        <td><code>casperjs.executable</code></td>
        <td>Complete path of the executable for CasperJS.</td>
        <td>None</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td><code>tests.directory</code></td>
        <td>Directory where the tests to execute are stored.</td>
        <td>None</td>
        <td>Yes</td>
    </tr>
    <tr>
        <td><code>ignoreTestFailures</code></td>
        <td>Do we ignore the tests failures. If yes, the plugin will not fail at the end if there was tests failures.</td>
        <td>false</td>
        <td>No</td>
    </tr>
    <tr>
        <td><code>include.javascript</code></td>
        <td>A flag to indicate if the *.js found in <code>tests.directory</code> should be executed.</td>
        <td>true</td>
        <td>No</td>
    </tr>
    <tr>
        <td><code>include.coffeescript</code></td>
        <td>A flag to indicate if the *.coffee found in <code>tests.directory</code> should be executed.</td>
        <td>true</td>
        <td>No</td>
    </tr>
    <tr>
        <td><code>verbose</code></td>
        <td>Set the *plugin* to be verbose during its execution. It will not impact the verbosity of the CasperJS execution.</td>
        <td>false</td>
        <td>No</td>
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
        <td>Set the value for the CasperJS option <code>--pre=[pre-test.js]</code>: will add the tests contained in pre-test.js before executing the test suite.</td>
    </tr>
    <tr>
        <td><code>post</code></td>
        <td>Set the value for the CasperJS option <code>--post=[post-test.js]</code>: will add the tests contained in post-test.js after having executed the whole test suite.</td>
    </tr>
    <tr>
        <td><code>includes</code></td>
        <td>Set the value for the CasperJS option <code>--includes=[foo.js,bar.js]</code>: will includes the foo.js and bar.js files before each test file execution.</td>
    </tr>
    <tr>
        <td><code>xunit</code></td>
        <td>Set the value for the CasperJS option <code>--xunit=[filename]</code>: will export test suite results in the specified xUnit XML file.</td>
    </tr>
    <tr>
        <td><code>logLevel</code></td>
        <td>Set the value for the CasperJS option --log-level=[logLevel]: sets the logging level (see http://casperjs.org/logging.html).</td>
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
</table>


## TODO

Here is a list of things that should be done to make this plugin awesome:

- Set default values for some parameters (```tests.directory``` for example)
- Manage sub-directories where files are included
- Manage a list of excludes / includes for the tests to run

## Issues / Enhancements

If you encounter issues or think about any kind of enhancements, you can add them in the [adequate section of the project](https://github.com/linsolas/casperjs-runner-maven-plugin/issues). Do not hesitate also to make Push Requests if you want to enhance this project.

## License

This plugin is licensed with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
