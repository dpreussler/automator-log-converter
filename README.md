[![Build Status](https://travis-ci.org/dpreussler/automator-log-converter.svg?branch=master)](https://travis-ci.org/dpreussler/automator-log-converter)

automator-log-converter
=======================

Android UI Automator to JUnit Format Converter

Reads Android UI automator file outout and write a JUNIT Xml file. For example for usage in CI server as Jenkins

input: file with output from automator (simply redirect it to file)
output: file with JUnit XML

usage: `java -jar uiautomator2junit.jar <filename>`


(c) Danny Preussler, 2012 - 2015

Contains Apache ANT. Android DDMLib, licensed under Apache 2.0

LICENSED UNDER THE APACHE LICENSE, VERSION 2.0 (THE "LICENSE"); YOU MAY NOT USE THIS FILE EXCEPT IN COMPLIANCE WITH THE LICENSE. YOU MAY OBTAIN A COPY OF THE LICENSE AT HTTP://WWW.APACHE.ORG/LICENSES/LICENSE-2.0.
