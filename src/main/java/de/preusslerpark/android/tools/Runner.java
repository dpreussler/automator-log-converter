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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tools.ant.util.FileUtils;

public class Runner {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("usage: convert <filename>");
            return;
        }

        convert(args);
    }


    private static void convert(String[] args) throws IOException, FileNotFoundException {

        String inFile = args[0];
        String pathsep = System.getProperty("file.separator");
        String path;
        if (inFile.contains(pathsep)) {
            path = inFile.substring(0, inFile.lastIndexOf(pathsep));
        } else {
            path = "./";
        }

        String testSuite = getPureFileName(inFile.substring(path.length() + pathsep.length(), inFile.length()));

        Converter.createConverForPath(testSuite, path + pathsep).convert(readEntireFile(inFile));
    }

    private static String getPureFileName(String inFile) {
        return inFile.split("\\.")[0];
    }


    private static String readEntireFile(String filename) throws IOException {
        FileReader in = new FileReader(filename);
        try {
            return FileUtils.readFully(in).replace("\r", "");    
        } finally {
            in.close();
        }
        
    }
}
