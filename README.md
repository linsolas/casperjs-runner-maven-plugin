casperjs-runner-maven-plugin
============================

This project aims to run [CasperJS](http://casperjs.org/) tests in a Maven build.

Now available on Maven central.
Add in your pom.xml:

```
<plugin>
    <groupId>com.github.linsolas</groupId>
    <artifactId>casperjs-runner-maven-plugin</artifactId>
    <version>1.0.1</version>
    <configuration></configuration>
</plugin>
```

## System requirements

This plugin has been tested on two environments :
- PhantomJS (v **1.8.2**) and CasperJS (v **1.0.4**) where installed.
- PhantomJS (v **1.9.7**) and CasperJS (v **1.1.0-beta3**) where installed.

## Usage

See the [plugin site](http://linsolas.github.io/casperjs-runner-maven-plugin/)

## Build

Download the sources, and build the plugin using the ```mvn clean install``` command. You can build this plugin using Maven 2.2.x or Maven 3.x, and a JDK 1.6.

## CI
[![Build Status Images](https://travis-ci.org/linsolas/casperjs-runner-maven-plugin.svg)](https://travis-ci.org/linsolas/casperjs-runner-maven-plugin)

## TODO

Here is a list of things that should be done to make this plugin awesome:

- ~~Set default values for some parameters (```tests.directory``` for example)~~
- ~~Manage sub-directories where files are included~~
- ~~Manage a list of excludes / includes for the tests to run~~
- ~~Publish to Maven Central~~
- ~~Maven site~~
- ~~Continuous Integration via [Travis CI](https://travis-ci.org/)~~

## Issues / Enhancements

If you encounter issues or think about any kind of enhancements, you can add them in the [adequate section of the project](https://github.com/linsolas/casperjs-runner-maven-plugin/issues). Do not hesitate also to make Push Requests if you want to enhance this project.

## License

This plugin is licensed with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
