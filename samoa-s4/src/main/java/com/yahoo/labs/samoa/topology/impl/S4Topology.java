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
import com.yahoo.labs.samoa.topology.AbstractTopology;

public class S4Topology extends AbstractTopology {
	
	// CASEY: it seems evaluationTask is not used. 
	// Remove it for now
    
//	private String _evaluationTask;

//    S4Topology(String topoName, String evalTask) {
//        super(topoName);
//    }
//
//    S4Topology(String topoName) {
//        this(topoName, null);
//    }

//    @Override
//    public void setEvaluationTask(String evalTask) {
//        _evaluationTask = evalTask;
//    }
//
//    @Override
//    public String getEvaluationTask() {
//        return _evaluationTask;
//    }
    
	S4Topology(String topoName) {
		super(topoName);
	}
	
    protected EntranceProcessingItem getEntranceProcessingItem() {
    	if (this.getEntranceProcessingItems() == null) return null;
    	if (this.getEntranceProcessingItems().size() < 1) return null;
    	// TODO: support multiple entrance PIs
    	return (EntranceProcessingItem)this.getEntranceProcessingItems().toArray()[0];
    }
}
