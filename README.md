[![Build Status](https://travis-ci.org/dpreussler/automator-log-converter.svg?branch=master)](https://travis-ci.org/dpreussler/automator-log-converter)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jodamob.android/automator-log-converter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jodamob.android/automator-log-converter)


automator-log-converter
=======================

Android UI Automator to JUnit Format Converter

Reads Android UI automator file output and write a JUNIT Xml file. For example for usage in CI server as Jenkins

input: file with output from automator (simply redirect it to file)
output: file with JUnit XML

usage: `java -jar uiautomator2junit.jar <filename>`


Gradle
======

get from maven central:
http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22automator-log-converter%22

```groovy
compile 'de.jodamob.android:automator-log-converter:1.5.0'
 
```


(c) Danny Preussler, 2012 - 2015

Contains Apache ANT. Android DDMLib, licensed under Apache 2.0

LICENSED UNDER THE APACHE LICENSE, VERSION 2.0 (THE "LICENSE"); YOU MAY NOT USE THIS FILE EXCEPT IN COMPLIANCE WITH THE LICENSE. YOU MAY OBTAIN A COPY OF THE LICENSE AT HTTP://WWW.APACHE.ORG/LICENSES/LICENSE-2.0.
