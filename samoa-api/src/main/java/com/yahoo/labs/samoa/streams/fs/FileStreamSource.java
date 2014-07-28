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

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * An interface for FileStream's source (Local FS, HDFS,...)
 * @author Casey
 */
public interface FileStreamSource extends Serializable {

	/**
	 * Init the source with file/directory path and file extension
	 * @param path
	 *            File or directory path
	 * @param ext
	 *            File extension to be used to filter files in a directory. 
	 *            If null, all files in the directory are accepted.
	 */
	public void init(String path, String ext);
	
	/**
	 * Reset the source
	 */
	public void reset() throws IOException;
	
	/**
	 * Retrieve InputStream for next file.
	 * This method will return null if we are at the last file 
	 * in the list.
	 * 
	 * @return InputStream for next file in the list
	 */
	public InputStream getNextInputStream();
	
	/**
	 * Retrieve InputStream for current file.
	 * The "current pointer" is moved forward
	 * with getNextInputStream method. So if there was no
	 * invocation of getNextInputStream, this method will
	 * return null.
	 * 
	 * @return InputStream for current file in the list
	 */
	public InputStream getCurrentInputStream();
}
