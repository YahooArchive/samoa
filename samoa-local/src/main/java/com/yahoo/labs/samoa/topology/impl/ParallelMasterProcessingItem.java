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

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.utils.PriorityThreadPoolExecutor;

/**
 * Main ProcessingItem in local mode with multithreading
 * It is the PI that appears in the topology and is responsible
 * for scheduling ContentEvent to WorkerPIs
 * @author Anh Thu Vu
 */

public class ParallelMasterProcessingItem extends ParallelProcessingItem {

	private final int parallelismHint;
	private IProcessingItem[] arrayProcessingItem;
	private int threadPoolOffset;
	private List<Stream> outputStreams;
	private ParallelEntranceProcessingItem epi = null;
	
	/*
	 * Constructor & Setup
	 */
	public ParallelMasterProcessingItem(Processor processor) {
		this(processor, 1);
	}
	
	public ParallelMasterProcessingItem(Processor processor, int parallelismHint) {
		super(processor);
		this.outputStreams = new ArrayList<Stream>();
		this.parallelismHint = parallelismHint;
		this.threadPoolOffset = 0;
	}
	
	@Override
	public void createWorkerProcessingItem() {
		// Introduce randomness so threads at the head of the pool is not overloaded
		this.threadPoolOffset = (int) (Math.random() * ParallelEngine.getNumberOfThreads());
		
		// Set up worker PIs with their processors
		if (this.arrayProcessingItem == null && this.getParalellism() > 0) {
			this.arrayProcessingItem = new IProcessingItem[this.getParalellism()];
			for (int i=0; i<this.getParalellism(); i++) {
				this.arrayProcessingItem[i] = 
						new ParallelWorkerProcessingItem(this.getProcessor().newProcessor(this.getProcessor()));
				this.arrayProcessingItem[i].getProcessor().onCreate(i);
			}
		}
	}
	
	/*
	 * Getters
	 */
	@Override
	public int getParalellism() {
		return parallelismHint;
	}
	
	public IProcessingItem getWorkerProcessingItem(int index) {
		if (this.arrayProcessingItem == null) return null;
		if (index < this.arrayProcessingItem.length && index >= 0)
			return this.arrayProcessingItem[index];
		return null;
	}
	
	public void updatePriorityLevel(int priority) {
		for (Stream stream:outputStreams) {
			if (stream instanceof ParallelStream) {
				ParallelStream pStream = (ParallelStream) stream;
				pStream.updatePriorityLevel(priority);
			}
		}
	}
	
	public void updateEntranceProcessingItem(ParallelEntranceProcessingItem epi) {
		if (this.epi != null) return;
		this.epi = epi;
		for (Stream stream:outputStreams) {
			if (stream instanceof ParallelStream) {
				ParallelStream pStream = (ParallelStream) stream;
				pStream.updateEntranceProcessingItem(epi);;
			}
		}
	}
	
	public void addOutputStream(Stream stream) {
		this.outputStreams.add(stream);
	}
	
	/*
	 * Connect to stream according to the scheduling type
	 */
	private ProcessingItem connectInputStream(Stream inputStream, int type) {
		ParallelStream stream = (ParallelStream) inputStream;
        stream.add(this, type, this.getParalellism());
        return this;
	}
	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
        return this.connectInputStream(inputStream, SHUFFLE);
    }

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
	    return this.connectInputStream(inputStream, GROUP_BY_KEY);
	}
	
	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
	    return this.connectInputStream(inputStream, BROADCAST);
	}
	
	/*
	 * Process the incoming ContentEvent
	 */
	@Override
	public void processEvent(ContentEvent event) {
		this.processEvent(event, 0, 0);
	}
	
	public void processEvent(ContentEvent event, int priorityLevel) {
		this.processEvent(event, priorityLevel, 0);
	}
	
	public void processEvent(ContentEvent event, int priorityLevel, int index) {
		PriorityThreadPoolExecutor pool = (PriorityThreadPoolExecutor) ParallelEngine.getExecutorService(index+threadPoolOffset);
		ParallelProcessingTask task = 
				new ParallelProcessingTask(this.getWorkerProcessingItem(index), event, priorityLevel, pool.nextSequenceNumber());
		pool.submit(task);
		if (pool.isQueueFull()) {
			this.epi.notifyQueueIsFull();
		}
	}

}
