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

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * ComponentFactory for running with multithreading in local mode
 * Written based on SimpleComponentFactory
 * @author Anh Thu Vu
 */
public class ParallelComponentFactory implements ComponentFactory {
	
	@Override
	public ProcessingItem createPi(Processor processor) {
		return this.createPi(processor,1);
	}

	@Override
	public ProcessingItem createPi(Processor processor, int parallelism) {
		ParallelMasterProcessingItem pi = new ParallelMasterProcessingItem(processor, parallelism);
		return pi;
	}

	@Override
	public EntranceProcessingItem createEntrancePi(Processor processor, TopologyStarter starter) {
		ParallelEntranceProcessingItem epi = new ParallelEntranceProcessingItem(processor, starter);
		return epi;
	}

	@Override
	public Stream createStream(IProcessingItem sourcePi) {
		ParallelStream stream = new ParallelStream(sourcePi);
		return stream;
	}

	@Override
	public Topology createTopology(String topoName) {
		ParallelTopology pt = new ParallelTopology(topoName);
		return pt;
	}

}
