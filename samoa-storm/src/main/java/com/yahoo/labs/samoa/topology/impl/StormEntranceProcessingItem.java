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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * EntranceProcessingItem implementation for Storm.
 * @author Arinto Murdopo
 *
 */
class StormEntranceProcessingItem implements StormTopologyNode, EntranceProcessingItem {

	private final Processor processor;
	private final StormEntranceSpout piSpout;
	private final String piSpoutUuidStr;
        
	private String name;
	
	StormEntranceProcessingItem(Processor processor, TopologyStarter starter){
		this(processor, starter, UUID.randomUUID().toString());
	}
	
	StormEntranceProcessingItem(Processor processor, TopologyStarter starter, String friendlyId){
		this.processor = processor;
		this.piSpoutUuidStr = friendlyId;
		this.piSpout = new StormEntranceSpout(processor, starter);	
	}
	
	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public void put(Instance inst) {
		// do nothing, we not need this method
	}
	
	@Override
	public void addToTopology(StormTopology topology, int parallelismHint){
		topology.getStormBuilder().setSpout(piSpoutUuidStr, piSpout, parallelismHint);	
	}
	
	@Override
	public StormStream createStream() {
		return piSpout.createStream(piSpoutUuidStr);
	}
	
	@Override
	public String getId() {
		return piSpoutUuidStr;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.insert(0, String.format("id: %s, ", piSpoutUuidStr));
		return sb.toString();
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Resulting Spout of StormEntranceProcessingItem
	 * @author Arinto Murdopo
	 *
	 */
	final static class StormEntranceSpout extends BaseRichSpout {
		
		private static final long serialVersionUID = -9066409791668954099L;
	
		private final Set<StormSpoutStream> streams;
                private final Processor processor;
		private final TopologyStarter starter;
		
		private transient SpoutStarter spoutStarter;
		private transient Executor spoutExecutors;
		private transient LinkedBlockingQueue<StormTupleInfo> tupleInfoQueue;

		private SpoutOutputCollector collector;
		
		StormEntranceSpout(Processor processor, TopologyStarter starter){
			this.streams = new HashSet<StormSpoutStream>();
			this.starter = starter;
                        this.processor = processor;
		}
		
		@Override
		public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			this.collector = collector;			
			this.tupleInfoQueue = new LinkedBlockingQueue<StormTupleInfo>();

			//Processor and this class share the same instance of stream
			for(StormSpoutStream stream: streams){
				stream.setSpout(this);
			}

                        this.processor.onCreate(context.getThisTaskId());
			this.spoutStarter = new SpoutStarter(this.starter);
			
			this.spoutExecutors = Executors.newSingleThreadExecutor();
			this.spoutExecutors.execute(spoutStarter);
		}

		@Override
		public void nextTuple() {
			try {
				StormTupleInfo tupleInfo = tupleInfoQueue.poll(50, TimeUnit.MILLISECONDS);
				if(tupleInfo != null){
					Values value = newValues(tupleInfo.getContentEvent());
					collector.emit(tupleInfo.getStormStream().getOutputId(),value);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			StormSpoutStream stream = new StormSpoutStream(piId);
			streams.add(stream);
			return stream;
		}
		
		void put(StormSpoutStream stream, ContentEvent contentEvent){
			tupleInfoQueue.add(new StormTupleInfo(stream, contentEvent));
		}
				
		private Values newValues(ContentEvent contentEvent){
			return new Values(contentEvent, contentEvent.getKey());
		}
		
		private final static class StormTupleInfo{
			
			private final StormStream stream;
			private final ContentEvent event;
			
			StormTupleInfo(StormStream stream, ContentEvent event){
				this.stream = stream;
				this.event = event;
			}
			
			public StormStream getStormStream(){
				return this.stream;
			}
			
			public ContentEvent getContentEvent(){
				return this.event;
			}
		}
		
		private final static class SpoutStarter implements Runnable{

			private final TopologyStarter topoStarter;
			
			SpoutStarter(TopologyStarter topoStarter){
				this.topoStarter = topoStarter;
			}
			
			@Override
			public void run() {
				this.topoStarter.start();
			}
		}
	}

	


}
