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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * Stream for multithreaded engine.
 * @author Anh Thu Vu
 *
 */
public class ThreadsStream implements Stream {
	private static final Logger logger = LoggerFactory.getLogger(ThreadsStream.class);
	
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

	@Override
	public synchronized void put(ContentEvent event) {
        ThreadsProcessingItem pi;
        for (StreamDestination destination:destinations) {
            pi = (ThreadsProcessingItem) destination.getProcessingItem();
            counter++;
            if (counter >= maxCounter) counter = 0;
            switch (destination.getPartitioningScheme()) {
            case SHUFFLE:
                pi.processEvent(event, counter%destination.getParallelism());
                break;
            case GROUP_BY_KEY:
            	if(event.getKey() == null) {
            		logger.info("Skipping event with null key:{}",event);
            		break;
            	}
            	// HashCodeBuilder object does not have reset() method
            	// So all objects that get appended will be included in the 
            	// computation of the hashcode. 
            	// To avoid initialize a HashCodeBuilder for each event,
            	// here I use the static method with reflection on the event's key
                int key = HashCodeBuilder.reflectionHashCode(event.getKey(), true) % destination.getParallelism();
                pi.processEvent(event, key);
                break;
            case BROADCAST:
                for (int p = 0; p < destination.getParallelism(); p++) {
                    pi.processEvent(event, p);
                }
                break;
            }
        }
	}

	@Override
	public String getStreamId() {
		return null;
	}

}
