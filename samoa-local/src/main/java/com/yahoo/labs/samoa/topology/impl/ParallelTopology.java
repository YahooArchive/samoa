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

import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * Multithreaded Topology in local mode
 * Written based on SimpleTopology
 * @author Anh Thu Vu
 */
public class ParallelTopology extends Topology {

	public String topologyName;
    private ParallelEntranceProcessingItem entrancePi;
    private TopologyStarter starter;

    /*
     * Constructor
     */
    ParallelTopology(String topoName) {
        this.topologyName = topoName;
    }

    /*
     * Getters
     */
    public TopologyStarter getTopologyStarter() {
        return starter;
    }
    
    public ParallelEntranceProcessingItem getEntranceProcessingItem() {
        return entrancePi;
    }

    @Override
    public void addEntrancePi(EntranceProcessingItem epi, TopologyStarter starter) {
        this.starter = starter;
        this.entrancePi = (ParallelEntranceProcessingItem) epi;
        this.addProcessingItem(epi);
    }
    
    public void createWorkerProcessingItems() {
    	for (IProcessingItem pi:this.processingItems) {
    		if (pi instanceof ParallelProcessingItem) {
    			ParallelProcessingItem mpi = (ParallelProcessingItem) pi;
    			mpi.createWorkerProcessingItem();
    		}
    	}
    }
    
    public void setupPriorityLevel() {
    	this.entrancePi.setupPriorityLevel();
    }
    
    public void setupEntranceProcessingItem() {
    	this.entrancePi.updateEntranceProcessingItem();
    }
}
