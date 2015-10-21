/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package de.preusslerpark.android.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.XMLConstants;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.DateUtils;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.android.ddmlib.testrunner.TestIdentifier;


/**
 * Prints XML output of the test to a specified Writer.
 * 
 * @author dpreussler, based on JUnitResultFormatter
 * @see FormatterElement
 */

public class XMLResultFormatter implements XMLConstants {

	private static final double ONE_SECOND = 1000.0;

	/** constant for unnnamed testsuites/cases */
	private static final String UNKNOWN = "unknown";

	private static DocumentBuilder getDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception exc) {
			throw new ExceptionInInitializerError(exc);
		}
	}

	/**
	 * The XML document.
	 */
	 private Document doc;
	 /**
	  * The wrapper for the whole testsuite.
	  */
	 private Element rootElement;
	 /**
	  * Element for the current test.
	  */
	 private Hashtable<TestIdentifier, Element> testElements = new Hashtable<TestIdentifier, Element>();
	 /**
	  * tests that failed.
	  */
	 private Hashtable<TestIdentifier, TestIdentifier> failedTests = new Hashtable<TestIdentifier, TestIdentifier>();
	 /**
	  * Timing helper.
	  */
	 private Hashtable<TestIdentifier, Long> testStarts = new Hashtable<TestIdentifier, Long>();
	 /**
	  * Where to write the log to.
	  */
	 private OutputStream out;

	 /** No arg constructor. */
	 public XMLResultFormatter() {
	 }

	 /** {@inheritDoc}. */
	 public void setOutput(OutputStream out) {
		 this.out = out;
	 }

	 /** {@inheritDoc}. */
	 public void setSystemOutput(String out) {
		 formatOutput(SYSTEM_OUT, out);
	 }

	 /** {@inheritDoc}. */
	 public void setSystemError(String out) {
		 formatOutput(SYSTEM_ERR, out);
	 }

	 /**
	  * The whole testsuite started.
	  * 
	  * @param suite
	  *            the testsuite.
	  */
	 public void startTestSuite(String n) {
		 doc = getDocumentBuilder().newDocument();
		 rootElement = doc.createElement(TESTSUITE);
		 rootElement.setAttribute(ATTR_NAME, n == null ? UNKNOWN : n);

		 // add the timestamp
		 final String timestamp = DateUtils.format(new Date(),
				 DateUtils.ISO8601_DATETIME_PATTERN);
		 rootElement.setAttribute(TIMESTAMP, timestamp);
		 // and the hostname.
		 rootElement.setAttribute(HOSTNAME, getHostname());

		 // Output properties
		 Element propsElement = doc.createElement(PROPERTIES);
		 rootElement.appendChild(propsElement);
	 }

	public void setTestCounts(int testCount, int testsFailed) {
	      rootElement.setAttribute("tests", String.valueOf(testCount));
	      rootElement.setAttribute("failures", String.valueOf(testsFailed));
	}

	 /**
	  * get the local hostname
	  * 
	  * @return the name of the local host, or "localhost" if we cannot work it out
	  */
	 protected String getHostname() {
		 try {
			 return InetAddress.getLocalHost().getHostName();
		 } catch (UnknownHostException e) {
			 return "localhost";
		 }
	 }

	 /**
	  * The whole testsuite ended.
	  * 
	  * @param suite
	  *            the testsuite.
	  * @throws BuildException
	  *             on error.
	  */
	 public void endTestSuite(String name, long time) throws BuildException {
		 // rootElement.setAttribute(ATTR_TESTS, "" + suite.runCount());
		 // rootElement.setAttribute(ATTR_FAILURES, "" + suite.failureCount());
		 // rootElement.setAttribute(ATTR_ERRORS, "" + suite.errorCount());
		 rootElement.setAttribute(ATTR_TIME, "" + (time / ONE_SECOND));
		 if (out != null) {
			 Writer wri = null;
			 try {
				 wri = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
				 wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
				 (new DOMElementWriter()).write(rootElement, wri, 0, "  ");
			 } catch (IOException exc) {
				 throw new BuildException("Unable to write log file " + exc.toString(), exc);
			 } finally {
				 if (wri != null) {
					 try {
						 wri.flush();
					 } catch (IOException ex) {
						 // ignore
					 }
				 }
				 if (out != System.out && out != System.err) {
					 FileUtils.close(wri);
				 }
			 }
		 }
	 }

	 /**
	  * Interface TestListener.
	  * <p>
	  * A new Test is started.
	  * 
	  * @param t
	  *            the test.
	  */
	 public void startTest(TestIdentifier t) {
		 testStarts.put(t, new Long(System.currentTimeMillis()));
	 }

	 /**
	  * Interface TestListener.
	  * <p>
	  * A Test is finished.
	  * 
	  * @param test
	  *            the test.
	  */
	 public void endTest(TestIdentifier test) {


		 Element currentTest = null;
		 if (!failedTests.containsKey(test)) {
			 currentTest = doc.createElement(TESTCASE);
			 String n = test.getTestName();
			 currentTest.setAttribute(ATTR_NAME,
					 n == null ? UNKNOWN : n);
			 // a TestSuite can contain Tests from multiple classes,
			 // even tests with the same name - disambiguate them.
			 currentTest.setAttribute(ATTR_CLASSNAME,
					 test.getClassName());
			 rootElement.appendChild(currentTest);
			 testElements.put(test, currentTest);
		 } else {
			 currentTest = (Element) testElements.get(test);
		 }

		 Long l = (Long) testStarts.get(test);
		 currentTest.setAttribute(ATTR_TIME,
				 "" + ((System.currentTimeMillis()
						 - l.longValue()) / ONE_SECOND));
	 }

	 /**
	  * Interface TestListener for JUnit &lt;= 3.4.
	  * <p>
	  * A Test failed.
	  * 
	  * @param test
	  *            the test.
	  * @param t
	  *            the exception.
	  */
	 public void addFailure(TestIdentifier test, Throwable t) {
		 formatError(FAILURE, test, t);
	 }

	 public void addFailure(TestIdentifier test, String message, String className, String strace) {
		 formatError(FAILURE, test, message, className, strace);
	 }


	 /**
	  * Interface TestListener for JUnit &gt; 3.4.
	  * <p>
	  * A Test failed.
	  * 
	  * @param test
	  *            the test.
	  * @param t
	  *            the assertion.
	  */
	 public void addFailure(TestIdentifier test, AssertionFailedError t) {
		 addFailure(test, (Throwable) t);
	 }

	 /**
	  * Interface TestListener.
	  * <p>
	  * An error occurred while running the test.
	  * 
	  * @param test
	  *            the test.
	  * @param t
	  *            the error.
	  */
	 public void addError(TestIdentifier test, Throwable t) {
		 formatError(ERROR, test, t);
	 }

	 private void formatError(String type, TestIdentifier test, Throwable t) {
		 formatError(type, test, t.getMessage(), t.getClass().getName(), JUnitTestRunner.getFilteredTrace(t));
	 }

	 private void formatError(String type, TestIdentifier test, String message, String className, String strace) {

		 strace = strace.replace("\r", ""); 
		 if (test != null) {
			 endTest(test);
			 failedTests.put(test, test);
		 }

		 Element nested = doc.createElement(type);
		 Element currentTest = null;
		 if (test != null) {
			 currentTest = (Element) testElements.get(test);
		 } else {
			 currentTest = rootElement;
		 }

		 currentTest.appendChild(nested);


		 if (message != null && message.length() > 0) {
			 nested.setAttribute(ATTR_MESSAGE, message);
		 }
		 nested.setAttribute(ATTR_TYPE, className);

		 Text trace = doc.createTextNode(strace);
		 nested.appendChild(trace);
	 }

	 private void formatOutput(String type, String output) {
		 Element nested = doc.createElement(type);
		 rootElement.appendChild(nested);
		 nested.appendChild(doc.createCDATASection(output));
	 }

} // XMLJUnitResultFormatter
