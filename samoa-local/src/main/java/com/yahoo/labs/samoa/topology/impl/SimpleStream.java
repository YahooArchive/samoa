/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.yahoo.labs.samoa.topology.AbstractStream;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * 
 * @author abifet
 */
class SimpleStream extends AbstractStream {
    private List<StreamDestination> destinations;
    private int maxCounter;
    private int eventCounter;

    SimpleStream(IProcessingItem sourcePi) {
    	super(sourcePi);
    	this.destinations = new LinkedList<>();
    	this.eventCounter = 0;
    	this.maxCounter = 1;
    }

    private int getNextCounter() {
    	if (maxCounter > 0 && eventCounter >= maxCounter) eventCounter = 0;
    	this.eventCounter++;
    	return this.eventCounter;
    }

    @Override
    public void put(ContentEvent event) {
    	this.put(event, this.getNextCounter());
    }
    
    private void put(ContentEvent event, int counter) {
    	SimpleProcessingItem pi;
        int parallelism;
        for (StreamDestination destination:destinations) {
            pi = (SimpleProcessingItem) destination.getProcessingItem();
            parallelism = destination.getParallelism();
            switch (destination.getPartitioningScheme()) {
            case SHUFFLE:
                pi.processEvent(event, counter % parallelism);
                break;
            case GROUP_BY_KEY:
                HashCodeBuilder hb = new HashCodeBuilder();
                hb.append(event.getKey());
                int key = hb.build() % parallelism;
                pi.processEvent(event, key);
                break;
            case BROADCAST:
                for (int p = 0; p < parallelism; p++) {
                    pi.processEvent(event, p);
                }
                break;
            }
        }
    }

    public void addDestination(StreamDestination destination) {
        this.destinations.add(destination);
        if (maxCounter <= 0) maxCounter = 1;
        maxCounter *= destination.getParallelism();
    }
}
