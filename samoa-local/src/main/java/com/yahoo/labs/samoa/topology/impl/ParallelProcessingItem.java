package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * Abstract class of ProcessingItem in local mode with multithreading
 * The actual implementations are in ParallelMasterProcessingItem
 * and ParallelWorkerProcessingItem
 * @author Anh Thu Vu
 */
public abstract class ParallelProcessingItem implements ProcessingItem {
	
	// 3 scheduling types
	public static final int SHUFFLE = 0;
    public static final int GROUP_BY_KEY = 1;
    public static final int BROADCAST = 2;
    
    // Each PI has one processor
    protected Processor processor;
    
    /*
     * Constructor & Setup methods
     */
    public ParallelProcessingItem() {}
    
    public ParallelProcessingItem(Processor processor) {
    	this.processor = processor;
    }
    
    public abstract void createWorkerProcessingItem();

    /*
     * Getters
     */
	@Override
	public Processor getProcessor() {
		return this.processor;
	}
	
	@Override
	public int getParalellism() {
		return 0;
	}

	/*
	 * Connect to stream, depend on the scheduling type
	 */
	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		return null;
	}

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
		return null;
	}

	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
		return null;
	}
	
	/*
	 *  Process the incoming event
	 */
	public void processEvent(ContentEvent event) {
		this.processor.process(event);
	}

}
