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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.AbstractStream;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * Stream for multithreaded engine.
 * @author Anh Thu Vu
 *
 */
public class ThreadsStream extends AbstractStream {
	
	private List<StreamDestination> destinations;
	private int counter = 0;
	private int maxCounter = 1;
	
	public ThreadsStream(IProcessingItem sourcePi) {
		destinations = new LinkedList<StreamDestination>();
	}
	
	public void addDestination(StreamDestination destination) {
		destinations.add(destination);
		maxCounter *= destination.getParallelism();
	}
	
	public List<StreamDestination> getDestinations() {
		return this.destinations;
	}
	
	private int getNextCounter() {
    	if (maxCounter > 0 && counter >= maxCounter) counter = 0;
    	this.counter++;
    	return this.counter;
    }

    @Override
    public synchronized void put(ContentEvent event) {
    	this.put(event, this.getNextCounter());
    }
    
    private void put(ContentEvent event, int counter) {
    	ThreadsProcessingItem pi;
        int parallelism;
        for (StreamDestination destination:destinations) {
            pi = (ThreadsProcessingItem) destination.getProcessingItem();
            parallelism = destination.getParallelism();
            switch (destination.getPartitioningScheme()) {
            case SHUFFLE:
            	pi.processEvent(event, counter%parallelism);
                break;
            case GROUP_BY_KEY:
            	pi.processEvent(event, getPIIndexForKey(event.getKey(), parallelism));
                break;
            case BROADCAST:
            	for (int p = 0; p < parallelism; p++) {
                    pi.processEvent(event, p);
                }
                break;
            }
        }
    }
	
	private static int getPIIndexForKey(String key, int parallelism) {
		// If key is null, return a default index: 0
		if (key == null) return 0;
		
		// HashCodeBuilder object does not have reset() method
    	// So all objects that get appended will be included in the 
    	// computation of the hashcode. 
    	// To avoid initialize a HashCodeBuilder for each event,
    	// here I use the static method with reflection on the event's key
		int index = HashCodeBuilder.reflectionHashCode(key, true) % parallelism;
		if (index < 0) {
			index += parallelism;
		}
		return index;
	}

}
