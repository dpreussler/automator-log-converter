package de.preusslerpark.android.tools;

import static org.junit.Assert.assertEquals;

import org.apache.tools.ant.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class OutputTest {
    
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
    
    String prefix = "foo";
	String suffix = ".bar";

    private void testByFile(String testSuiteName) throws IOException, FileNotFoundException {
    	File tmpTestInput = extractResourceFile(testSuiteName + ".txt");
    	File tmpXml = extractResourceFile(testSuiteName + ".xml");
    	File tmpTestOutput = File.createTempFile(prefix, suffix);
    	
    	Converter.create(tmpTestInput, tmpTestOutput, testSuiteName).convert(new XMLResultFormatter() {
            @Override
            protected String getHostname() {
                return "DENB0738.local";
            }
        });
    	String actual = readFullyAndTimeless(new InputStreamReader(new FileInputStream(tmpTestOutput)));
        String expected = readFullyAndTimeless(new InputStreamReader(new FileInputStream(tmpXml)));
        
        assertEquals(expected, actual);
        
        tmpTestInput.deleteOnExit();
        tmpXml.deleteOnExit();
        tmpTestOutput.deleteOnExit();
    }
    
    /**
     * extracts a resource file and writes it to the temporary folder
     * @param resourceFileName
     * @return
     * @throws IOException
     */
    private File extractResourceFile(String resourceFileName) throws IOException {
    	 InputStream is = this.getClass().getResourceAsStream("./" + resourceFileName);
    	 
    	 File tmp = File.createTempFile(prefix, suffix);
    	 OutputStream os = null;
    	 try {
    		 os = new FileOutputStream(tmp);
    		 int readBytes;
    		 byte[] buffer = new byte[4096];
    		 while ((readBytes = is.read(buffer)) > 0) {
    			 os.write(buffer, 0, readBytes);
    		 }
    		 return tmp;
    	 }
    	 catch (Exception e) {
    	 }
    	 finally {
    		 if (os != null) {
    			 os.close();
    		 }
    	 }
    	 return null;
    }

    private String readFullyAndTimeless(Reader reader) throws IOException {
         try {
             return FileUtils.readFully(reader)
            		 .replaceAll("time=\"[0-9]*.[0-9]*\"", "")
            		 .replaceAll("timestamp=\"[^\"]*\"", "");
         } finally {
             reader.close();
         }
        
    }

}
