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

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.streams.PrequentialSourceProcessor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * EntranceProcessingItem in local mode with multithreading
 * For now, it's exactly the same as SimpleEntranceProcessingItem
 * @author Anh Thu Vu
 */
public class ParallelEntranceProcessingItem implements EntranceProcessingItem {
	private final Processor processor;
	private final TopologyStarter topologyStarter;
	private List<Stream> outputStreams;
	
    /*
     * Constructor
     */
    public ParallelEntranceProcessingItem(Processor processor, TopologyStarter starter) {
        this.processor = processor;
        this.topologyStarter = starter;
        this.outputStreams = new ArrayList<Stream>();
    }

	@Override
	public Processor getProcessor() {
		return processor;
	}
	
	public TopologyStarter getTopologyStarter() {
		return topologyStarter;
	}

	@Override
	public void put(Instance inst) {
		// do nothing
	}
	
	/*
	 * Record the list of output streams
	 */
	public void addOutputStream(Stream stream) {
		this.outputStreams.add(stream);
	}
	
	/*
	 * Get notified when a queue is full
	 * Currently only work with PrequentialSourceProcessor
	 */
	public void notifyQueueIsFull() {
		if (this.processor instanceof PrequentialSourceProcessor) {
			((PrequentialSourceProcessor) this.processor).notifyQueueIsFull();
		}
	}
	
	/*
	 * Setup methods
	 */
	public void setupPriorityLevel() {
		for (Stream stream:outputStreams) {
			if (stream instanceof ParallelStream) {
				ParallelStream pStream = (ParallelStream) stream;
				pStream.updatePriorityLevel(1);
			}
		}
	}
	
	public void updateEntranceProcessingItem() {
		for (Stream stream:outputStreams) {
			if (stream instanceof ParallelStream) {
				ParallelStream pStream = (ParallelStream) stream;
				pStream.updateEntranceProcessingItem(this);
			}
		}
	}
	
}
