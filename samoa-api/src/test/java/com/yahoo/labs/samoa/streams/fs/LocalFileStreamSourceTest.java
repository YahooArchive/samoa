package com.yahoo.labs.samoa.streams.fs;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.SecurityException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

public class LocalFileStreamSourceTest {
	private static final String BASE_DIR = "localfsTest";
	private static final int NUM_FILES_IN_DIR = 4;
	private static final int NUM_NOISE_FILES_IN_DIR = 2;
	
	private LocalFileStreamSource streamSource;

	@Before
	public void setUp() throws Exception {
		streamSource = new LocalFileStreamSource();
		
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(new File(BASE_DIR));
	}

	@Test
	public void testInitWithSingleFileAndExtension() {
		// write input file
		writeSimpleFiles(BASE_DIR,"txt",1);
				
		// init with path to input file
		File inFile = new File(BASE_DIR,"1.txt");
		String inFilePath = inFile.getAbsolutePath();
		streamSource.init(inFilePath, "txt");
				
		//assertions
		assertEquals("Size of filePaths is not correct.", 1,streamSource.getFilePathListSize(),0);
		String fn = streamSource.getFilePathAt(0);
		assertEquals("Incorrect file in filePaths.",inFilePath,fn);
	}
	
	@Test
	public void testInitWithSingleFileAndNullExtension() {
		// write input file
		writeSimpleFiles(BASE_DIR,"txt",1);
				
		// init with path to input file
		File inFile = new File(BASE_DIR,"1.txt");
		String inFilePath = inFile.getAbsolutePath();
		streamSource.init(inFilePath, null);
				
		//assertions
		assertEquals("Size of filePaths is not correct.", 1,streamSource.getFilePathListSize(),0);
		String fn = streamSource.getFilePathAt(0);
		assertEquals("Incorrect file in filePaths.",inFilePath,fn);
	}
	
	@Test
	public void testInitWithFolderAndExtension() {
		// write input file
		writeSimpleFiles(BASE_DIR,null,NUM_NOISE_FILES_IN_DIR);
		writeSimpleFiles(BASE_DIR,"txt",NUM_FILES_IN_DIR);
				
		// init with path to input dir
		File inDir = new File(BASE_DIR);
		String inDirPath = inDir.getAbsolutePath();
		streamSource.init(inDirPath, "txt");
				
		//assertions
		assertEquals("Size of filePaths is not correct.", NUM_FILES_IN_DIR,streamSource.getFilePathListSize(),0);
		Set<String> filenames = new HashSet<String>();
		for (int i=1; i<=NUM_FILES_IN_DIR; i++) {
			String expectedFn = (new File(inDirPath,Integer.toString(i)+".txt")).getAbsolutePath();
			filenames.add(expectedFn);
		}
		for (int i=0; i< NUM_FILES_IN_DIR; i++) {
			String fn = streamSource.getFilePathAt(i);
			assertTrue("Incorrect file in filePaths:"+fn,filenames.contains(fn));
		}
	}
	
	@Test
	public void testInitWithFolderAndNullExtension() {
		// write input file
		writeSimpleFiles(BASE_DIR,null,NUM_FILES_IN_DIR);
				
		// init with path to input dir
		File inDir = new File(BASE_DIR);
		String inDirPath = inDir.getAbsolutePath();
		streamSource.init(inDirPath, null);
				
		//assertions
		assertEquals("Size of filePaths is not correct.", NUM_FILES_IN_DIR,streamSource.getFilePathListSize(),0);
		Set<String> filenames = new HashSet<String>();
		for (int i=1; i<=NUM_FILES_IN_DIR; i++) {
			String expectedFn = (new File(inDirPath,Integer.toString(i))).getAbsolutePath();
			filenames.add(expectedFn);
		}
		for (int i=0; i< NUM_FILES_IN_DIR; i++) {
			String fn = streamSource.getFilePathAt(i);
			assertTrue("Incorrect file in filePaths:"+fn,filenames.contains(fn));
		}
	}
	
	/*
	 * getNextInputStream tests
	 */
	@Test
	public void testGetNextInputStream() {
		// write input files & noise files
		writeSimpleFiles(BASE_DIR,"txt",NUM_FILES_IN_DIR);
					
		// init with path to input dir
		streamSource.init(BASE_DIR, "txt");
				
		// call getNextInputStream & assertions
		Set<String> contents = new HashSet<String>();
		for (int i=1; i<=NUM_FILES_IN_DIR; i++) {
			contents.add(Integer.toString(i));
		}
		for (int i=0; i< NUM_FILES_IN_DIR; i++) {
			InputStream inStream = streamSource.getNextInputStream();
			assertNotNull("Unexpected end of input stream list.",inStream);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(inStream));
			String inputRead = null;
			try {
				inputRead = rd.readLine();
			} catch (IOException ioe) {
				fail("Fail reading from stream at index:"+i + ioe.getMessage());
			}
			assertTrue("File content is incorrect.",contents.contains(inputRead));
			Iterator<String> it = contents.iterator();
			while (it.hasNext()) {
				if (it.next().equals(inputRead)) {
					it.remove();
					break;
				}
			}
		}
		
		// assert that another call to getNextInputStream will return null
		assertNull("Call getNextInputStream after the last file did not return null.",streamSource.getNextInputStream());
	}
	
	/*
	 * getCurrentInputStream tests
	 */
	public void testGetCurrentInputStream() {
		// write input files & noise files
		writeSimpleFiles(BASE_DIR,"txt",NUM_FILES_IN_DIR);
							
		// init with path to input dir
		streamSource.init(BASE_DIR, "txt");
						
		// call getNextInputStream, getCurrentInputStream & assertions
		for (int i=0; i<= NUM_FILES_IN_DIR; i++) { // test also after-end-of-list
			InputStream inStream1 = streamSource.getNextInputStream();
			InputStream inStream2 = streamSource.getCurrentInputStream();
			assertSame("Incorrect current input stream.",inStream1, inStream2);
		}
	}
	
	/*
	 * reset tests
	 */
	public void testReset() {
		// write input files & noise files
		writeSimpleFiles(BASE_DIR,"txt",NUM_FILES_IN_DIR);

		// init with path to input dir
		streamSource.init(BASE_DIR, "txt");

		// Get the first input string
		InputStream firstInStream = streamSource.getNextInputStream();
		String firstInput = null;
		assertNotNull("Unexpected end of input stream list.",firstInStream);

		BufferedReader rd1 = new BufferedReader(new InputStreamReader(firstInStream));
		try {
			firstInput = rd1.readLine();
		} catch (IOException ioe) {
			fail("Fail reading from stream at index:0" + ioe.getMessage());
		}

		// call getNextInputStream a few times
		streamSource.getNextInputStream();

		// call reset, call next, assert that output is 1 (the first file)
		try {
			streamSource.reset();
		} catch (IOException ioe) {
			fail("Fail resetting stream source." + ioe.getMessage());
		}

		InputStream inStream = streamSource.getNextInputStream();
		assertNotNull("Unexpected end of input stream list.",inStream);

		BufferedReader rd2 = new BufferedReader(new InputStreamReader(inStream));
		String inputRead = null;
		try {
			inputRead = rd2.readLine();
		} catch (IOException ioe) {
			fail("Fail reading from stream at index:0" + ioe.getMessage());
		}
		assertEquals("File content is incorrect.",firstInput,inputRead);
	}
	
	private void writeSimpleFiles(String path, String ext, int numOfFiles) {
		// Create folder
		File folder = new File(path);
		if (!folder.exists()) {
			try{
		        folder.mkdir();
		    } catch(SecurityException se){
		    	fail("Failed creating directory:"+path+se);
		    }
		}
		
		// Write files
		for (int i=1; i<=numOfFiles; i++) {
			String fn = null;
			if (ext != null) {
				fn = Integer.toString(i) + "."+ ext;
			} else {
				fn = Integer.toString(i);
			}
			
			try {
				FileWriter fwr = new FileWriter(new File(path,fn));
				fwr.write(Integer.toString(i));
				fwr.close();
			} catch (IOException ioe) {
				fail("Fail writing to input file: "+ fn + " in directory: " + path + ioe.getMessage());
			}
		}
	}

}
