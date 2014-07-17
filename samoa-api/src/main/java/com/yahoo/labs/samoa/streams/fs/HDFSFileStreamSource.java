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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

/**
 * Source for FileStream for HDFS files
 * @author Casey
 */
public class HDFSFileStreamSource implements FileStreamSource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3887354805787167400L;
	
	private transient InputStream fileStream;
    private transient Configuration config;
    private List<String> filePaths;
    private int currentIndex;
	
	public HDFSFileStreamSource(){
		this.currentIndex = -1;
	}
	
	public void init(String path, String ext) {
		this.setupConfig();
		this.filePaths = new ArrayList<String>();
		Path hdfsPath = new Path(path);
        FileSystem fs;
        try {
        	fs = FileSystem.get(config);
        	FileStatus fileStat = fs.getFileStatus(hdfsPath);
        	if (fileStat.isDirectory()) {
        		FileStatus[] filesInDir = fs.globStatus(new Path(path.toString(),"*."+ext));
        		for (int i=0; i<filesInDir.length; i++) {
        			if (filesInDir[i].isFile()) {
        				System.out.println("ADD FILE:"+filesInDir[i].getPath().toString());
        				filePaths.add(filesInDir[i].getPath().toString());
        			}
    			}
        	}
        	else {
        		this.filePaths.add(path);
        	}
        }
        catch(IOException ioe) {
            throw new RuntimeException("Failed getting list of files at:"+path,ioe);
        }
        
		this.currentIndex = -1;
	}
	
	private void setupConfig() {
		String hadoopHome = System.getenv("HADOOP_HOME");
        config = new Configuration();
        if (hadoopHome != null) {
        	java.nio.file.Path coreSitePath = FileSystems.getDefault().getPath(hadoopHome, "etc/hadoop/core-site.xml");
        	java.nio.file.Path hdfsSitePath = FileSystems.getDefault().getPath(hadoopHome, "etc/hadoop/hdfs-site.xml");
            config.addResource(new Path(coreSitePath.toAbsolutePath().toString()));
            config.addResource(new Path(hdfsSitePath.toAbsolutePath().toString()));
        }
	}
	
	public void reset() throws IOException {
		this.currentIndex = -1;
		this.closeFileStream();
	}

	private void closeFileStream() {
        IOUtils.closeStream(fileStream);
	}

	public InputStream getNextInputStream() {
		this.closeFileStream();
		if (this.currentIndex >= (this.filePaths.size()-1)) return null;
		
		this.currentIndex++;
		String filePath = this.filePaths.get(currentIndex);
		
		Path hdfsPath = new Path(filePath);
        FileSystem fs;
        try {
        	fs = FileSystem.get(config);
            fileStream = fs.open(hdfsPath);
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
	
	public boolean isAtLastFile() {
		return (this.currentIndex >= (this.filePaths.size()-1));
	}
}
