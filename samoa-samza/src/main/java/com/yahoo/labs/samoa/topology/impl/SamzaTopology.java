package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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

import java.util.HashSet;
import java.util.Set;

import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.AbstractTopology;

/**
 * Topology for Samza
 * 
 * @author Anh Thu Vu
 */
public class SamzaTopology extends AbstractTopology {
	private int procItemCounter;
	
	public SamzaTopology(String topoName) {
		super(topoName);
		procItemCounter = 0;
	}
	
	@Override
	public void addProcessingItem(IProcessingItem procItem, int parallelism) {
		super.addProcessingItem(procItem, parallelism);
		SamzaProcessingNode samzaPi = (SamzaProcessingNode) procItem;
		samzaPi.setName(this.getTopologyName()+"-"+Integer.toString(procItemCounter));
		procItemCounter++;
	}
	
	/*
	 * Gets the set of ProcessingItems, excluding EntrancePIs
	 * Used by SamzaConfigFactory as the config for EntrancePIs and
	 * normal PIs are different
	 */
	public Set<IProcessingItem> getNonEntranceProcessingItems() throws Exception {
		Set<IProcessingItem> copiedSet = new HashSet<IProcessingItem>();
		copiedSet.addAll(this.getProcessingItems());
		boolean result = copiedSet.removeAll(this.getEntranceProcessingItems());
		if (!result) {
			throw new Exception("Failed extracting the set of non-entrance processing items");
		}
		return copiedSet;
	}
}