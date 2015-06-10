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

import java.io.File;
import java.io.IOException;

public class Runner {

    public static void main(String[] args) {

        if (args.length == 1) {
           convert(args[0]);
        }
        else {
           System.out.println("usage: convert <filename>");
        }
    }


    private static void convert(String inputFilePath) {
        File inputFile = new File(inputFilePath);
        String parentPath = getParentPath(inputFile);
        String testSuite = getTestSuiteName(inputFile);
        String outputFilePath = parentPath + File.separator + testSuite + ".xml";
        File outputFile = createOutput(outputFilePath);
        Converter.create(inputFile, outputFile).convert();
    }

    static String getParentPath(File file) {
      String filePath = file.getAbsolutePath();
      String fileName = file.getName();
      return filePath.replace(fileName, "");
   }

    static String getTestSuiteName(File file) {
      String fileName = file.getName();
      System.out.println(fileName);
      System.out.println(fileName.substring(0,fileName.lastIndexOf('.')));
      return fileName.substring(0,fileName.lastIndexOf('.'));
    }

    static File createOutput(String outputFilePath) {
      File outputFile = new File(outputFilePath);
      if (outputFile.exists()) {
         if (!outputFile.delete()) {
            throw new RuntimeException("could not delete existing output file");
         }
      }
      try {
         if (!outputFile.createNewFile()) {
            throw new RuntimeException("could not create the new output file");
         }
      }
      catch (IOException e) {
      }
      return outputFile;
   }
}
