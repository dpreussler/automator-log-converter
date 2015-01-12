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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import com.android.ddmlib.testrunner.TestIdentifier;

public class Converter {

    private final String testSuiteName;
    private final String outPath;

    public static Converter createConverForFile(String testSuiteName, String outPath) {
        return new Converter(testSuiteName, outPath, true);
    }
    
    public static Converter createConverForPath(String testSuiteName, String outPath) {
        return new Converter(testSuiteName, outPath, false);
    }

    private Converter(String testSuiteName, String outPath, boolean isFilename) {
        this.testSuiteName = testSuiteName;
        this.outPath = isFilename ? outPath : outPath + testSuiteName + ".xml";
    }

    public void convert(String streamToRead) throws FileNotFoundException, IOException {
        FileOutputStream currentFile = new FileOutputStream(outPath);
        final XMLResultFormatter outputter = new XMLResultFormatter();
        InstrumentationResultParser parser = createParser(testSuiteName, outputter);
        outputter.setOutput(currentFile);
        outputter.startTestSuite(testSuiteName);

        String[] lines = streamToRead.split("\n");;
        parser.processNewLines(lines);
        parser.done();
        outputter.endTestSuite(testSuiteName, 0);
        currentFile.close();
    }

    private InstrumentationResultParser createParser(String testSuite, final XMLResultFormatter outputter) {


        ITestRunListener listener = new ITestRunListener() {

            @Override
            public void testEnded(TestIdentifier test, Map<String, String> arg1) {
                System.out.println("testEnded " + test);
                outputter.endTest(test);
            }

            @Override
            public void testFailed(TestFailure arg0, TestIdentifier test, String arg2) {
                System.out.println("testFailed " + arg0 + "/" + arg2);

                BufferedReader reader = new BufferedReader(new StringReader(arg2));
                try {
                    String error = reader.readLine();
                    String[] errorSeperated = error.split(":");
                    outputter.addFailure(test, errorSeperated.length > 1 ? errorSeperated[1].trim() : "Failed", errorSeperated[0].trim(), arg2.substring(error.length()));
                } catch (IOException e) {
                    e.printStackTrace();
                    outputter.addFailure(test, e);
                }

            }

            @Override
            public void testRunEnded(long elapsedTime, Map<String, String> arg1) {
                // System.out.println("testRunEnded " + elapsedTime + "/" + arg1);
                // outputter.endTestSuite(currentSuite, elapsedTime);

            }

            @Override
            public void testRunFailed(String name) {
                System.out.println("testRunFailed " + name);
                // outputter.endTestSuite(name, 0);

            }

            @Override
            public void testRunStarted(String name, int arg1) {
                // System.out.println("testRunStarted " + name + "/" + arg1);
                // if (name.equals(currentSuite)) {
                // name = name + "1";
                // }
                // currentSuite = name;
                // try {
                // if (currentFile != null) {
                // currentFile.close();
                // }
                // currentFile = new FileOutputStream(outPath + currentSuite + ".xml");
                // }
                // catch (IOException e) {
                // }
                //
                // outputter.setOutput(currentFile);
                // outputter.startTestSuite(currentSuite);
            }

            @Override
            public void testRunStopped(long elapsedTime) {
                // outputter.endTestSuite(currentSuite, elapsedTime);
            }

            @Override
            public void testStarted(final TestIdentifier test) {
                System.out.println("testStarted " + test.toString());
                outputter.startTest(test);


            }
        };
        return new InstrumentationResultParser(testSuite, listener);
    }

}
