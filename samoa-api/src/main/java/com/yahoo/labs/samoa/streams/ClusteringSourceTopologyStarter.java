package com.yahoo.labs.samoa.streams;

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
import com.yahoo.labs.samoa.topology.Stream;

/**
 * Clustering Topology Starter is a wrapper of ClusteringSourceProcessor so that
 * the Entrance PI could start the execution of the topology.
 * 
 * @author Arinto Murdopo
 *
 */
public class ClusteringSourceTopologyStarter implements TopologyStarter {

	private static final long serialVersionUID = -2323666993205665265L;
	
	private final ClusteringSourceProcessor psp;
	private Stream inputStream;
        private Stream evalStream;
	private final int numInstanceSent;
        private double samplingThreshold;
	
	public ClusteringSourceTopologyStarter(ClusteringSourceProcessor psp, int numInstanceSent, double samplingThreshold){
		this.psp = psp;
		this.numInstanceSent = numInstanceSent;
                this.samplingThreshold = samplingThreshold;
	}
	@Override
	public void start() {
		this.psp.sendInstances(inputStream, evalStream, numInstanceSent, samplingThreshold);
	}
	
	public void setInputStream(Stream inputStream)
	{
		this.inputStream = inputStream;
	}
        
        public void setEvalStream(Stream evalStream)
	{
		this.evalStream = evalStream;
	}
        

}
