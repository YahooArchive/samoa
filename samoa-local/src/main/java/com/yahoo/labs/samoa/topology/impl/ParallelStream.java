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
import com.yahoo.labs.samoa.topology.Stream;

/**
 * Stream in local mode with multithreading
 * @author Anh Thu Vu
 */
public class ParallelStream implements Stream {

	private IProcessingItem sourcePi;
    private List<IProcessingItem> listProcessingItem;
    private List<Integer> listTypeStream;
    private List<Integer> listParallelism;
    private int shuffleCounter;
    private int priorityLevel = 0;
    
    /*
     * Constructor
     */
    public ParallelStream(IProcessingItem sourcePi) {
        this.sourcePi = sourcePi;
        if (sourcePi instanceof ParallelEntranceProcessingItem) {
        	ParallelEntranceProcessingItem pSourcePi = (ParallelEntranceProcessingItem) sourcePi;
        	pSourcePi.addOutputStream(this);
        }
        else if (sourcePi instanceof ParallelMasterProcessingItem) {
        	ParallelMasterProcessingItem pSourcePi = (ParallelMasterProcessingItem) sourcePi;
        	pSourcePi.addOutputStream(this);
        }
        this.listProcessingItem = new LinkedList<IProcessingItem>();
        this.listTypeStream = new LinkedList<Integer>();
        this.listParallelism = new LinkedList<Integer>();
        shuffleCounter = 0;
    }
    
    /*
     * Getters
     */
    @Override
	public String getStreamId() {
		return null;
	}
    
    public IProcessingItem getSourcePi() {
    	return sourcePi;
    }
    
    public int getPriorityLevel() {
    	return priorityLevel;
    }
    
    /*
     * Setters
     */
    public void updatePriorityLevel(int priority) {
    	if (this.priorityLevel > 0) return; // should need to be set ONCE only
    	this.priorityLevel = priority;
    	for (IProcessingItem pi:listProcessingItem) {
    		if (pi instanceof ParallelMasterProcessingItem) {
    			ParallelMasterProcessingItem mpi = (ParallelMasterProcessingItem) pi;
    			mpi.updatePriorityLevel(priority+1);
    		}
    	}
    }
    
    public void updateEntranceProcessingItem(ParallelEntranceProcessingItem epi) {
    	for (IProcessingItem pi:listProcessingItem) {
    		if (pi instanceof ParallelMasterProcessingItem) {
    			ParallelMasterProcessingItem mpi = (ParallelMasterProcessingItem) pi;
    			mpi.updateEntranceProcessingItem(epi);
    		}
    	}
    }
    
    /*
     * Add destination PI to stream
     */
    public void add(IProcessingItem destinationPi, int type, int parallelism) {
        this.listProcessingItem.add(destinationPi);
        this.listTypeStream.add(type);
        this.listParallelism.add(parallelism);
    }
    
    /*
     * Pass incoming ContentEvent to destination PIs
     */
	@Override
	public void put(ContentEvent event) {
        ParallelMasterProcessingItem pi;
        int type;
        int parallelism = 1;
        int index = 0;
        for (int i = 0; i < this.listProcessingItem.size(); i++) {
        	pi = (ParallelMasterProcessingItem) this.listProcessingItem.get(i);
        	type = this.listTypeStream.get(i);
        	parallelism = this.listParallelism.get(i);
        	switch(type) {
        	case ParallelProcessingItem.SHUFFLE:
        		index = shuffleCounter % parallelism;
        		pi.processEvent(event, priorityLevel, index);
        		shuffleCounter++;
        		break;
        	case ParallelProcessingItem.GROUP_BY_KEY:
        		HashCodeBuilder hb = new HashCodeBuilder();
    	        hb.append(event.getKey());
    	        index = hb.build() % parallelism;
    	        pi.processEvent(event, priorityLevel, index);
    	        break;
        	case ParallelProcessingItem.BROADCAST:
        		for (index=0; index<parallelism; index++) {
        			pi.processEvent(event, priorityLevel, index);
        		}
        		break;
        	}
        	
        }
	}

	

}
