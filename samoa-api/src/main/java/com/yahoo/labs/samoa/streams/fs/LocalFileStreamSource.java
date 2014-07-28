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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

/**
 * Source for FileStream for local files
 * @author Casey
 */
public class LocalFileStreamSource implements FileStreamSource {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3986511547525870698L;
	
	private transient InputStream fileStream;
    private List<String> filePaths;
    private int currentIndex;
	
	public LocalFileStreamSource(){
		this.currentIndex = -1;
	}
	
	public void init(String path, String ext) {
		this.filePaths = new ArrayList<String>();
		File fileAtPath = new File(path);
		if (fileAtPath.isDirectory()) {
			File[] filesInDir = fileAtPath.listFiles(new FileExtensionFilter(ext));
			for (int i=0; i<filesInDir.length; i++) {
				filePaths.add(filesInDir[i].getAbsolutePath());
			}
		}
		else {
			this.filePaths.add(path);
		}
		this.currentIndex = -1;
	}
	
	public void reset() throws IOException {
		this.currentIndex = -1;
		this.closeFileStream();
	}
	
	private void closeFileStream() {
		if (fileStream != null) {
			try {
				fileStream.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public InputStream getNextInputStream() {
		this.closeFileStream();
		
		if (this.currentIndex >= (this.filePaths.size()-1)) return null;
		
		this.currentIndex++;
		String filePath = this.filePaths.get(currentIndex);
		
		File file = new File(filePath);
        try {
        	fileStream = new FileInputStream(file);
        }
        catch(IOException ioe) {
            this.closeFileStream();
            throw new RuntimeException("Failed opening file:"+filePath,ioe);
        }
        
        return fileStream;
	}

	public InputStream getCurrentInputStream() {
		return fileStream;
	}
	
	protected int getFilePathListSize() {
		if (filePaths != null)
			return filePaths.size();
		return 0;
	}
	
	protected String getFilePathAt(int index) {
		if (filePaths != null && filePaths.size() > index)
			return filePaths.get(index);
		return null;
	}
	
	private class FileExtensionFilter implements FilenameFilter {
		private String extension;
		FileExtensionFilter(String ext) {
			extension = ext;
		}
		
		@Override
		public boolean accept(File dir, String name) {
			File f = new File(dir,name);
			if (extension == null)
				return f.isFile();
			else
				return f.isFile() && name.toLowerCase().endsWith("."+extension);
	    }
	}
}
