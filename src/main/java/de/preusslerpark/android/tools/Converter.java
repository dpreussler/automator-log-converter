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

import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import com.android.ddmlib.testrunner.TestIdentifier;

import org.apache.tools.ant.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class Converter {

    private final String testSuiteName;
    private final File inputFile;
    private final File outputFile;
    private int testCount;
    private int testsFailed;

    /**
     * converter factory creates a converter instance given an input file to be converted
     * and an output file 
     * @param inputFile
     * @param outputFile
     * @return
     */
    public static Converter create(File inputFile, File outputFile) {
      return new Converter(inputFile, outputFile);
    }

    static Converter create(File inputFile, File outputFile, String testSuiteName) {
        return new Converter(inputFile, outputFile, testSuiteName);
    }

    private Converter(File inputFile, File outputFile) {
        this(inputFile, outputFile, Runner.getTestSuiteName(inputFile));
    }

    private Converter(File inputFile, File outputFile, String testSuiteName) {
      this.testSuiteName = testSuiteName;
      this.inputFile = inputFile;
      this.outputFile = outputFile;
   }
    
   private static String readEntireFile(String filename) throws IOException {
       FileReader in = new FileReader(filename);
       try {
           return FileUtils.readFully(in).replace("\r", "");
       } finally {
           in.close();
       }
   }

   /**
    * reads the input file and creates an output file
    */
    public void convert() {
        convert(new XMLResultFormatter());
    }

    public void convert(XMLResultFormatter outputter) {
        testsFailed = 0;
        testCount = 0;
        try {
          tryConvert(outputter);
        }
        catch( IOException e) {
        }
    }

    private void tryConvert(XMLResultFormatter outputter) throws IOException {
        String streamToRead = readEntireFile(inputFile.getAbsolutePath());
        FileOutputStream currentFile = new FileOutputStream(outputFile.getAbsolutePath());
        InstrumentationResultParser parser = createParser(testSuiteName, outputter);
        outputter.setOutput(currentFile);
        outputter.startTestSuite(testSuiteName);

        String[] lines = streamToRead.split("\n");
        ;
        parser.processNewLines(lines);
        parser.done();
        outputter.setTestCounts(testCount, testsFailed);
        outputter.endTestSuite(testSuiteName, 0);
        currentFile.close();
    }

    private InstrumentationResultParser createParser(String testSuite, final XMLResultFormatter outputter) {


        ITestRunListener listener = new ITestRunListener() {

            @Override
            public void testEnded(TestIdentifier test, Map<String, String> arg1) {
                System.out.println("testEnded " + test);
                outputter.endTest(test);
                testCount++;
            }

            @Override
            public void testFailed(TestIdentifier test, String arg2) {
                System.out.println("testFailed " + arg2);

                BufferedReader reader = new BufferedReader(new StringReader(arg2));
                try {
                    String error = reader.readLine();
                    String[] errorSeperated = error.split(":", 2);
                    outputter.addFailure(test, errorSeperated.length > 1 ? errorSeperated[1].trim() : "Failed", errorSeperated[0].trim(), arg2.substring(error.length()));
                } catch (IOException e) {
                    e.printStackTrace();
                    outputter.addFailure(test, e);
                }
                testsFailed++;
            }

            @Override
            public void testRunEnded(long elapsedTime, Map<String, String> arg1) {
            }

            @Override
            public void testRunFailed(String name) {
                System.out.println("testRunFailed " + name);
            }

            @Override
            public void testRunStarted(String name, int arg1) {
            }

            @Override
            public void testRunStopped(long elapsedTime) {
            }
            
            @Override
            public void testIgnored(TestIdentifier test) {
            }
            
            @Override
            public void testAssumptionFailure(TestIdentifier test, String trace) {
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
