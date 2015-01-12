package de.preusslerpark.android.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tools.ant.util.FileUtils;
import org.junit.Before;
import org.junit.Test;


public class OutputTest {
    
    File tmp;
    
    @Before
    public void setup() throws IOException {
        tmp = File.createTempFile("logconverter", null);
        tmp.deleteOnExit();
    }
    
    @Test
    public void test_cut_off_messages() throws IOException {
        testByFile("test_cut_off_messages");
    }
    
    
    @Test
    public void test_file_with_one_exception() throws IOException {
        testByFile("test_file_with_one_exception");
    }
    
    @Test
    public void test_file_with_two_exceptions() throws IOException {
        testByFile("test_file_with_two_exceptions");
    }

    private void testByFile(String testSuiteName) throws IOException, FileNotFoundException {
        Converter converter = Converter.createConverForFile(testSuiteName, tmp.toString());
        String in = FileUtils.readFully(new InputStreamReader(this.getClass().getResourceAsStream(testSuiteName+".txt")));
        String expected = readFullyAndTimeless(new InputStreamReader(this.getClass().getResourceAsStream(testSuiteName+".xml")));
        converter.convert(in);
        assertEquals(expected, readFullyAndTimeless(new FileReader(tmp)));
    }

    private String readFullyAndTimeless(Reader reader) throws IOException {
         try {
             return FileUtils.readFully(reader).replaceAll("time=\"[0-9]*.[0-9]*\"", "").replaceAll("timestamp=\"[^\"]*\"", "");     
         } finally {
             reader.close();
         }
        
    }

}
