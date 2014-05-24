//package com.yahoo.labs.samoa.streams;
//
///*
// * #%L
// * SAMOA
// * %%
// * Copyright (C) 2013 Yahoo! Inc.
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import com.yahoo.labs.samoa.core.TopologyStarter;
//import com.yahoo.labs.samoa.topology.Stream;
//
//public class StreamSourceTopologyStarter implements TopologyStarter {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -8478975754185196904L;
//
//	private final StreamSourceProcessor streamSourceP;
//	private Stream inputStream;
//	private final int numTrainInstances;
//	private final int numTestInstances;
//	private final int numEvaluationInstances;
//	
//	public StreamSourceTopologyStarter(StreamSourceProcessor processor, 
//			int numTrainInstances, int numTestInstances, int numEvaluationInstances){
//		this.streamSourceP = processor;
//		this.numTrainInstances = numTrainInstances;
//		this.numTestInstances = numTestInstances;
//		this.numEvaluationInstances = numEvaluationInstances;
//	}
//	
//	@Override
//	public void start() {
//		streamSourceP.sendInstances(inputStream, this.numTrainInstances, true,false,
//				this.numEvaluationInstances);
//		System.out.println("Instances sent to train");
//		// Testing
//		streamSourceP.sendInstances(inputStream, this.numTestInstances, false,true,
//				this.numEvaluationInstances);
//		System.out.println("Instances sent to test");
//		// End of Evaluation
//		streamSourceP.sendEndEvaluationInstance(inputStream);
//
//	}
//	
//	public void setInputStream(Stream inputStream){
//		this.inputStream = inputStream;
//	}
//
//}
//FIXME delete this class