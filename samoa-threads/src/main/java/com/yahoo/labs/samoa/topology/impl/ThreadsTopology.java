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

import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * Topology for multithreaded engine.
 * @author Anh Thu Vu
 *
 */
public class ThreadsTopology extends Topology {
	/*
	 * TODO: support multiple entrance PIs
	 */
    public void run() {
    	ThreadsEntranceProcessingItem entrancePi = (ThreadsEntranceProcessingItem) this.getEntranceProcessingItem();
    	if (entrancePi == null) 
    		throw new IllegalStateException("You need to set entrance PI before running the topology.");
    		
    	this.setupProcessingItemInstances();
    	entrancePi.getProcessor().onCreate(0); // id=0 as it's not used in multithreading mode
    	entrancePi.startSendingEvents();
    }
    
    public ThreadsTopology(String topoName) {
    	super(topoName);
    }

    protected EntranceProcessingItem getEntranceProcessingItem() {
    	if (this.entranceProcessingItems == null || this.entranceProcessingItems.size() < 1)
    		return null;
    	
    	return (EntranceProcessingItem) this.entranceProcessingItems.toArray()[0];
    }

    @Override
    protected void addEntranceProcessingItem(EntranceProcessingItem epi) {
        super.addEntranceProcessingItem(epi);
    }
    
    /* 
     * Tell all the ThreadsProcessingItems to create & init their 
     * replicas (ThreadsProcessingItemInstance)
     */
    private void setupProcessingItemInstances() {
    	for (IProcessingItem pi:this.processingItems) {
    		if (pi instanceof ThreadsProcessingItem) {
    			ThreadsProcessingItem tpi = (ThreadsProcessingItem) pi;
    			tpi.setupInstances();
    		}
    	}
    }
}
