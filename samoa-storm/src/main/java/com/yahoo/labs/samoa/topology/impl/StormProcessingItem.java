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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.impl.StormStream.InputStreamId;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * ProcessingItem implementation for Storm.
 * @author Arinto Murdopo
 *
 */
class StormProcessingItem implements ProcessingItem, StormTopologyNode {
	
	private final Processor processor;
	
	private final ProcessingItemBolt piBolt;	
	private BoltDeclarer piBoltDeclarer;
	private final String piBoltUuidStr;
	
	//TODO: should we put parallelism hint here? 
	//imo, parallelism hint only declared when we add this PI in the topology
	//open for dicussion :p
	private final int parallelismHint;
		
	StormProcessingItem(Processor processor, int parallelismHint){
		this(processor, UUID.randomUUID().toString(), parallelismHint);
	}
	
	StormProcessingItem(Processor processor, String friendlyId, int parallelismHint){
		this.processor = processor;
		this.piBolt = new ProcessingItemBolt(processor);
		this.piBoltUuidStr = friendlyId;
		this.parallelismHint = parallelismHint;
	}

	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		StormStream stormInputStream = (StormStream) inputStream;
		InputStreamId inputId = stormInputStream.getInputId();
		
		piBoltDeclarer.shuffleGrouping(inputId.getComponentId(),
				inputId.getStreamId());
		return this;
	}

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
		StormStream stormInputStream = (StormStream) inputStream;
		InputStreamId inputId = stormInputStream.getInputId();
		
		piBoltDeclarer.fieldsGrouping(
				inputId.getComponentId(), 
				inputId.getStreamId(), 
				new Fields(StormSamoaUtils.KEY_FIELD));

		return this;
	}
	
	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
		StormStream stormInputStream = (StormStream) inputStream;
		InputStreamId inputId = stormInputStream.getInputId();
		
		piBoltDeclarer.allGrouping(
				inputId.getComponentId(), 
				inputId.getStreamId());
		
		return this;
	}
	
	@Override
	public void addToTopology(StormTopology topology, int parallelismHint) {
		if(piBoltDeclarer != null){
			//throw exception that one PI only belong to one topology
		}else{		
			TopologyBuilder stormBuilder = topology.getStormBuilder();
			this.piBoltDeclarer = stormBuilder.setBolt(this.piBoltUuidStr, 
					this.piBolt, parallelismHint);
		}	
	}

	@Override
	public StormStream createStream() {
		return piBolt.createStream(piBoltUuidStr);
	}

	@Override
	public String getId() {
		return piBoltUuidStr;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.insert(0, String.format("id: %s, ", piBoltUuidStr));
		return sb.toString();
	}
		
	private final static class ProcessingItemBolt extends BaseRichBolt{
		
		private static final long serialVersionUID = -6637673741263199198L;
		
		private final Set<StormBoltStream> streams;
		private final Processor processor;
		
		private OutputCollector collector;
		
		ProcessingItemBolt(Processor processor){
			this.streams = new HashSet<StormBoltStream>();
			this.processor = processor;
		}
		
		@Override
		public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;	
			//Processor and this class share the same instance of stream
			for(StormBoltStream stream: streams){
				stream.setCollector(this.collector);
			}
			
			this.processor.onCreate(context.getThisTaskId());
		}

		@Override
		public void execute(Tuple input) {
			Object sentObject = input.getValue(0);
			ContentEvent sentEvent = (ContentEvent)sentObject;
			processor.process(sentEvent);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			for(StormStream stream: streams){
				declarer.declareStream(stream.getOutputId(), 
						new Fields(StormSamoaUtils.CONTENT_EVENT_FIELD,
								StormSamoaUtils.KEY_FIELD));
			}
		}
		
		StormStream createStream(String piId){
			StormBoltStream stream = new StormBoltStream(piId);
			streams.add(stream);
			return stream;
		}
	}
	
	//not used by samoa-storm
	@Override
	public int getParalellism() {
		return this.parallelismHint;
	}

	//not used by samoa-storm
//	@Override
//	public void setName(String name) {
//		//do nothing
//	}
}


