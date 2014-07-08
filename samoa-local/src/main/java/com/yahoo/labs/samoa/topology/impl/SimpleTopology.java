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

import com.yahoo.labs.samoa.topology.AbstractTopology;

public class SimpleTopology extends AbstractTopology {
	SimpleTopology(String name) {
		super(name);
	}

	public void run() {
    	if (this.getEntranceProcessingItems() == null)
    		throw new IllegalStateException("You need to set entrance PI before running the topology.");
    	if (this.getEntranceProcessingItems().size() != 1)
    		throw new IllegalStateException("SimpleTopology supports 1 entrance PI only. Number of entrance PIs is "+this.getEntranceProcessingItems().size());
    	
    	SimpleEntranceProcessingItem entrancePi = (SimpleEntranceProcessingItem) this.getEntranceProcessingItems().toArray()[0];
    	entrancePi.getProcessor().onCreate(0); // id=0 as it is not used in simple mode
        entrancePi.startSendingEvents();
    }
}
