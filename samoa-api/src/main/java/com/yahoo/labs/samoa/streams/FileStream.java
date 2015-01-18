package com.yahoo.labs.samoa.streams;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.javacliparser.ClassOption;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.moa.core.InstanceExample;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;
import com.yahoo.labs.samoa.streams.fs.FileStreamSource;

/**
 * InstanceStream for files 
 * (Abstract class: subclass this class for different file formats)
 * @author Casey
 */
public abstract class FileStream extends AbstractOptionHandler implements InstanceStream {
	/**
    *
    */
   private static final long serialVersionUID = 3028905554604259130L;

   public ClassOption sourceTypeOption = new ClassOption("sourceType",
           's', "Source Type (HDFS, local FS)", FileStreamSource.class,
           "LocalFileStreamSource");
   
   protected transient FileStreamSource fileSource;
   protected transient Reader fileReader;
   protected Instances instances;
   
   protected boolean hitEndOfStream;
   private boolean hasStarted;

   /*
    * Constructors
    */
   public FileStream() {
	   this.hitEndOfStream = false;
   }
   
   /*
    * implement InstanceStream
    */
   @Override
   public InstancesHeader getHeader() {
	   return new InstancesHeader(this.instances);
   }

   @Override
   public long estimatedRemainingInstances() {
	   return -1;
   }

   @Override
   public boolean hasMoreInstances() {
	   return !this.hitEndOfStream;
   }
   
   @Override
   public InstanceExample nextInstance() {
	   if (this.getLastInstanceRead() == null) {
		   readNextInstanceFromStream();
	   }
	   InstanceExample prevInstance = this.getLastInstanceRead();
	   readNextInstanceFromStream();
	   return prevInstance;
   }

   @Override
   public boolean isRestartable() {
           return true;
   }

   @Override
   public void restart() {
	   reset();
	   hasStarted = false;
   }

   protected void reset() {
	   try {
		   if (this.fileReader != null)
			   this.fileReader.close();
		   
		   fileSource.reset();
	   }
	   catch (IOException ioe) {
		   throw new RuntimeException("FileStream restart failed.", ioe);
	   }
	   
	   if (!getNextFileReader()) {
		   hitEndOfStream = true;
		   throw new RuntimeException("FileStream is empty.");
	   }
	   
       this.instances = new Instances(this.fileReader, 1, -1);
       this.instances.setClassIndex(this.instances.numAttributes() - 1);
   }
   
   protected boolean getNextFileReader() {
	   if (this.fileReader != null) 
		   try {
			   this.fileReader.close();
		   } catch (IOException ioe) {
			   ioe.printStackTrace();
		   }
	   
	   InputStream inputStream = this.fileSource.getNextInputStream();
	   if (inputStream == null)
		   return false;

	   this.fileReader = new BufferedReader(new InputStreamReader(inputStream));
	   return true;
   }
   
   protected boolean readNextInstanceFromStream() {
	   if (!hasStarted) {
		   this.reset();  
		   hasStarted = true;
	   }
	   
	   while (true) {
		   if (readNextInstanceFromFile()) return true;

		   if (!getNextFileReader()) {
			   this.hitEndOfStream = true;
			   return false;
		   }
	   }
   }
   
   /**
    * Read next instance from the current file and assign it to
    * lastInstanceRead.
    * @return true if it was able to read next instance and
    * 		  false if it was at the end of the file
    */
   protected abstract boolean readNextInstanceFromFile();
   
   protected abstract InstanceExample getLastInstanceRead();
   
   @Override
   public void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
	   this.fileSource = sourceTypeOption.getValue();
	   this.hasStarted = false;
   }
}
