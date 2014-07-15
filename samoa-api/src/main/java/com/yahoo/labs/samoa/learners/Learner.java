package com.yahoo.labs.samoa.learners;

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
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

import java.io.Serializable;
import java.util.Set;

/**
 * The Interface Classifier.
 * Initializing Classifier should initalize PI to connect the Classifier with the input stream 
 * and initialize result stream so that other PI can connect to the classification result of this classifier
 */

public interface Learner extends Serializable{

	/**
	 * Inits the Learner object.
	 *
	 * @param topologyBuilder the topology builder
	 * @param dataset the dataset
         * @param parallelism the parallelism
	 */	
	public void init(TopologyBuilder topologyBuilder, Instances dataset, int parallelism);
	
    /**
	 * Gets the input processing item.
	 *
	 * @return the input processing item
	 */
	public Processor getInputProcessor();

	
	/**
	 * Gets the result streams
	 *
	 * @return the set of result streams
	 */
	public Set<Stream> getResultStreams();
}
