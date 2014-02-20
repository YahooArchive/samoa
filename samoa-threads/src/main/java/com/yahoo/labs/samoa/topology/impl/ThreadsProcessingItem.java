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
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.utils.EventAllocationType;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsProcessingItem implements ProcessingItem {
	
	private Processor processor;
	private int parallelismHint;
	private List<ThreadsWorkerProcessingItem> listWorkerPi;
	private int offset;
	
	public ThreadsProcessingItem(Processor processor, int parallelismHint) {
		this.processor = processor;
		this.parallelismHint = parallelismHint;
		this.offset = (int) (Math.random()*ThreadsEngine.getNumberOfThreads());
	}

	@Override
	public Processor getProcessor() {
		return processor;
	}

	private ProcessingItem addInputStream(Stream inputStream, EventAllocationType type) {
		ThreadsStream stream = (ThreadsStream) inputStream;
		stream.addDestination(this, this.parallelismHint, type);
		return this;
	}
	
	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.SHUFFLE);
	}

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.GROUP_BY_KEY);
	}

	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.BROADCAST);
	}

	@Override
	public int getParalellism() {
		return this.parallelismHint;
	}
	
	public void setupWorkers() {
		this.listWorkerPi = new ArrayList<ThreadsWorkerProcessingItem>(parallelismHint);
		for (int i=0; i<this.parallelismHint; i++) {
			Processor newProcessor = this.processor.newProcessor(this.processor);
			newProcessor.onCreate(i + 1);
			this.listWorkerPi.add(new ThreadsWorkerProcessingItem(newProcessor, this.offset + i));
		}
	}
	
	public List<ThreadsWorkerProcessingItem> getWorkerProcessingItems() {
		return this.listWorkerPi;
	}
	
	public void processEvent(ContentEvent event, int counter) {
		if (this.listWorkerPi == null || this.listWorkerPi.size() < this.parallelismHint) {
			throw new IllegalStateException("ThreadsWorkerProcessingItem(s) need to be setup before process any event (ThreadsTopology.start()).");
		}
		
		ThreadsWorkerProcessingItem workerPi = this.listWorkerPi.get(counter);
		ThreadsEventRunnable runnable = new ThreadsEventRunnable(workerPi, event);
		ThreadsEngine.getThreadWithIndex(workerPi.getThreadIndex()).submit(runnable);
	}

}
