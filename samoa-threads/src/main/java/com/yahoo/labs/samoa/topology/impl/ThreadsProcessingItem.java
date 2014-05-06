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
import com.yahoo.labs.samoa.topology.AbstractProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.utils.PartitioningScheme;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * ProcessingItem for multithreaded engine.
 * @author Anh Thu Vu
 *
 */
public class ThreadsProcessingItem extends AbstractProcessingItem {
	// Replicas of the ProcessingItem.
	// When ProcessingItem receives an event, it assigns one
	// of these replicas to process the event.
	private List<ThreadsProcessingItemInstance> piInstances;
	
	// Each replica of ProcessingItem is assigned to one of the
	// available threads in a round-robin fashion, i.e.: each 
	// replica is associated with the index of a thread. 
	// Each ProcessingItem has a random offset variable so that
	// the allocation of PI replicas to threads are spread evenly
	// among all threads.
	private int offset;
	
	/*
	 * Constructor
	 */
	public ThreadsProcessingItem(Processor processor, int parallelismHint) {
		super(processor, parallelismHint);
		this.offset = (int) (Math.random()*ThreadsEngine.getNumberOfThreads());
	}
	
	public List<ThreadsProcessingItemInstance> getProcessingItemInstances() {
		return this.piInstances;
	}

	/*
	 * Connects to streams
	 */
	@Override
    protected ProcessingItem addInputStream(Stream inputStream, PartitioningScheme scheme) {
		StreamDestination destination = new StreamDestination(this, this.getParallelism(), scheme);
		((ThreadsStream) inputStream).addDestination(destination);
		return this;
	}

	/*
	 * Process the received event.
	 */
	public void processEvent(ContentEvent event, int counter) {
		if (this.piInstances == null || this.piInstances.size() < this.getParallelism())
			throw new IllegalStateException("ThreadsWorkerProcessingItem(s) need to be setup before process any event (i.e. in ThreadsTopology.start()).");
		
		ThreadsProcessingItemInstance piInstance = this.piInstances.get(counter);
		ThreadsEventRunnable runnable = new ThreadsEventRunnable(piInstance, event);
		ThreadsEngine.getThreadWithIndex(piInstance.getThreadIndex()).submit(runnable);
	}
	
	/*
	 * Setup the replicas of this PI. 
	 * This should be called after the topology is set up (all Processors and PIs are
	 * setup and connected to the respective streams) and before events are sent.
	 */
	public void setupInstances() {
		this.piInstances = new ArrayList<ThreadsProcessingItemInstance>(this.getParallelism());
		for (int i=0; i<this.getParallelism(); i++) {
			Processor newProcessor = this.getProcessor().newProcessor(this.getProcessor());
			newProcessor.onCreate(i + 1);
			this.piInstances.add(new ThreadsProcessingItemInstance(newProcessor, this.offset + i));
		}
	}

}
