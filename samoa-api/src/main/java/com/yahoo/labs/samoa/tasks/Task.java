package com.yahoo.labs.samoa.tasks;

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

import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * Task interface, the mother of all SAMOA tasks!
 */
public interface Task {

	/**
	 * Initialize this SAMOA task, 
	 * i.e. create and connect ProcessingItems and Streams
	 * and initialize the topology
	 */
	public void init();	
	
	/**
	 * Return the final topology object to be executed in the cluster
	 * @return topology object to be submitted to be executed in the cluster
	 */
	public Topology getTopology();
	
    // /**
    // * Return the entrance processor to start SAMOA topology
    // * The logic to start the topology should be implemented here
    // * @return entrance processor to start the topology
    // */
    // public TopologyStarter getTopologyStarter();
	
	/**
	 * Sets the factory.
	 * TODO: propose to hide factory from task, 
	 * i.e. Task will only see TopologyBuilder, 
	 * and factory creation will be handled by TopologyBuilder
	 *
	 * @param factory the new factory
	 */
	public void setFactory(ComponentFactory factory) ;
	
}