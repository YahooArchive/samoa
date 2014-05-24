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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.yahoo.labs.samoa.core.TopologyStarter;
//import com.yahoo.labs.samoa.topology.Stream;
//
///**
// * 
// * Topology starter used for development and experimenting.
// * 
// * @author severien
// *
// */
//public final class SingleStreamTopologyStarter implements TopologyStarter {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4507308931128039592L;
//
//	private static final Logger logger = LoggerFactory
//			.getLogger(SingleStreamTopologyStarter.class);
//
//	private Stream inputStream;
//	StreamSourceProcessor sourceProcessor;
//	private final int numInstances;
//	private final int numberEvaluations;
//
//	public SingleStreamTopologyStarter(StreamSourceProcessor processor,
//			int numInstances, int numberEvaluations) {
//		this.sourceProcessor = processor;
//		this.numInstances = numInstances;
//		this.numberEvaluations = numberEvaluations;
//	}
//
//	@Override
//	public void start() {
//		this.sourceProcessor.sendInstances(this.inputStream, this.numInstances,
//				false, true, this.numberEvaluations);
//
//	}
//
//	public void setInputStream(final Stream stream) {
//		this.inputStream = stream;
//	}
//
//}
//FIXME delete this class