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

import backtype.storm.topology.TopologyBuilder;

import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * Adaptation of SAMOA topology in samoa-storm
 * @author Arinto Murdopo
 *
 */
public class StormTopology extends Topology {
	
	private TopologyBuilder builder;
	private final String topologyName;
	
	public StormTopology(String topologyName){
		super();
		this.builder = new TopologyBuilder();
		this.topologyName = topologyName;
	}
	
	@Override
	protected void addProcessingItem(IProcessingItem procItem, int parallelismHint){
		StormTopologyNode stormNode = (StormTopologyNode) procItem;
		stormNode.addToTopology(this, parallelismHint);
		super.addProcessingItem(procItem, parallelismHint);
	}
	
	public TopologyBuilder getStormBuilder(){
		return builder;
	}
	
	public String getTopologyName(){
		return topologyName;
	}
}
