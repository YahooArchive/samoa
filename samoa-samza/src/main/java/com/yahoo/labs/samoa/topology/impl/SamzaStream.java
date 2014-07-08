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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.AbstractStream;
import com.yahoo.labs.samoa.utils.PartitioningScheme;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * Stream for SAMOA on Samza
 * 
 * @author Anh Thu Vu
 */
public class SamzaStream extends AbstractStream implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_SYSTEM_NAME = "kafka";
	
	private List<SamzaSystemStream> systemStreams;
	private transient MessageCollector collector;
	private String systemName;
	
	/*
	 * Constructor
	 */
	public SamzaStream(IProcessingItem sourcePi) {
		super(sourcePi);
		this.systemName = DEFAULT_SYSTEM_NAME;
		// Get name/id for this stream
		SamzaProcessingNode samzaPi = (SamzaProcessingNode) sourcePi;
		int index = samzaPi.addOutputStream(this);
		this.setStreamId(samzaPi.getName()+"-"+Integer.toString(index));
		// init list of SamzaSystemStream
		systemStreams = new ArrayList<SamzaSystemStream>();
	}
	
	/*
	 * System name (Kafka)
	 */
	public void setSystemName(String systemName) {
		this.systemName = systemName;
		for (SamzaSystemStream systemStream:systemStreams) {
			systemStream.setSystem(systemName);
		}
	}

	public String getSystemName() {
		return this.systemName;
	}

	/*
	 * Add the PI to the list of destinations. 
	 * Return the name of the corresponding SystemStream.
	 */
	public SamzaSystemStream addDestination(StreamDestination destination) {
		PartitioningScheme scheme = destination.getPartitioningScheme();
		int parallelism = destination.getParallelism();
		
		SamzaSystemStream resultStream = null;
		for (int i=0; i<systemStreams.size(); i++) {
			// There is an existing SystemStream that matches the settings. 
			// Do not create a new one
			if (systemStreams.get(i).isSame(scheme, parallelism)) {
				resultStream = systemStreams.get(i);
			}
		}
		
		// No existing SystemStream match the requirement
		// Create a new one 
		if (resultStream == null) {
			String topicName = this.getStreamId() + "-" + Integer.toString(systemStreams.size());
			resultStream = new SamzaSystemStream(this.systemName,topicName,scheme,parallelism);
			systemStreams.add(resultStream);
		}
		
		return resultStream;
	}
	
	public void setCollector(MessageCollector collector) {
		this.collector = collector;
	}
	
	public MessageCollector getCollector(){
		return this.collector;
	}
	
	public void onCreate() {
		for (SamzaSystemStream stream:systemStreams) {
			stream.initSystemStream();
		}
	}
	
	/*
	 * Implement Stream interface
	 */
	@Override
	public void put(ContentEvent event) {
		for (SamzaSystemStream stream:systemStreams) {
			stream.send(collector,event);
		}
	}
	
	public List<SamzaSystemStream> getSystemStreams() {
		return this.systemStreams;
	}
	
	/**
	 * SamzaSystemStream wrap around a Samza's SystemStream 
	 * It contains the info to create a Samza stream during the
	 * constructing process of the topology and
	 * will create the actual Samza stream when the topology is submitted
	 * (invoking initSystemStream())
	 * 
	 * @author Anh Thu Vu
	 */
	public static class SamzaSystemStream implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String system;
		private String stream;
		private PartitioningScheme scheme;
		private int parallelism;
		
		private transient SystemStream actualSystemStream = null;
		
		/*
		 * Constructors
		 */
		public SamzaSystemStream(String system, String stream, PartitioningScheme scheme, int parallelism) {
			this.system = system;
			this.stream = stream;
			this.scheme = scheme;
			this.parallelism = parallelism;
		}
		
		public SamzaSystemStream(String system, String stream, PartitioningScheme scheme) {
			this(system, stream, scheme, 1);
		}
		
		/*
		 * Setters
		 */
		public void setSystem(String system) {
			this.system = system;
		}
		
		/*
		 * Getters
		 */
		public String getSystem() {
			return this.system;
		}
		
		public String getStream() {
			return this.stream;
		}
		
		public PartitioningScheme getPartitioningScheme() {
			return this.scheme;
		}
		
		public int getParallelism() {
			return this.parallelism;
		}

		public boolean isSame(PartitioningScheme scheme, int parallelismHint) {
			return (this.scheme == scheme && this.parallelism == parallelismHint);
		}
		
		/*
		 * Init the actual Samza stream
		 */
		public void initSystemStream() {
			actualSystemStream = new SystemStream(this.system, this.stream);
		}
		
		/*
		 * Send a ContentEvent
		 */
		public void send(MessageCollector collector, ContentEvent contentEvent) {
			if (actualSystemStream == null) 
				this.initSystemStream();
			
			switch(this.scheme) {
			case SHUFFLE:
				this.sendShuffle(collector, contentEvent);
				break;
			case GROUP_BY_KEY:
				this.sendGroupByKey(collector, contentEvent);
				break;
			case BROADCAST:
				this.sendBroadcast(collector, contentEvent);
				break;
			}
		}
		
		/*
		 * Helpers
		 */
		private synchronized void sendShuffle(MessageCollector collector, ContentEvent event) {
			collector.send(new OutgoingMessageEnvelope(this.actualSystemStream, event));
		}
		
		private void sendGroupByKey(MessageCollector collector, ContentEvent event) {
			collector.send(new OutgoingMessageEnvelope(this.actualSystemStream, event.getKey(), null, event));
		}

		private synchronized void sendBroadcast(MessageCollector collector, ContentEvent event) {
			for (int i=0; i<parallelism; i++) {
				collector.send(new OutgoingMessageEnvelope(this.actualSystemStream, i, null, event));
			}
		}	
	}
}