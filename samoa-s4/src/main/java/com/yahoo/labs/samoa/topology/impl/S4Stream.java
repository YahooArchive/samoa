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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.s4.base.KeyFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.AbstractStream;

/**
 * S4 Platform specific stream.
 * 
 * @author severien
 *
 */
public class S4Stream extends AbstractStream {

	public static final int SHUFFLE = 0;
	public static final int GROUP_BY_KEY = 1;
	public static final int BROADCAST = 2;

	private static final Logger logger = LoggerFactory.getLogger(S4Stream.class);

	private S4DoTask app;
	private int processingItemParalellism;
	private int shuffleCounter;

	private static final String NAME = "STREAM-";
	private static int OBJ_COUNTER = 0;
	
	/* The stream list */
	public List<StreamType> streams;

	public S4Stream(S4DoTask app) {
		super();
		this.app = app;
		this.processingItemParalellism = 1;
		this.shuffleCounter = 0;
		this.streams = new ArrayList<StreamType>();
		this.setStreamId(NAME+OBJ_COUNTER);
		OBJ_COUNTER++;
	}
	
	public S4Stream(S4DoTask app, S4ProcessingItem pi) {
		super();
		this.app = app;
		this.processingItemParalellism = 1;
		this.shuffleCounter = 0;
		this.streams = new ArrayList<StreamType>();
		this.setStreamId(NAME+OBJ_COUNTER);
		OBJ_COUNTER++;
		
	}

	/**
	 * 
	 * @return
	 */
	public int getParallelism() {
		return processingItemParalellism;
	}

	public void setParallelism(int parallelism) {
		this.processingItemParalellism = parallelism;
	}

	public void addStream(String streamID, KeyFinder<S4Event> finder,
			S4ProcessingItem pi, int type) {
		String streamName = streamID +"_"+pi.getName(); 
		org.apache.s4.core.Stream<S4Event> stream = this.app.createStream(
				streamName, pi);
		stream.setName(streamName);
		logger.debug("Stream name S4Stream {}", streamName);
		if (finder != null)
			stream.setKey(finder);
		this.streams.add(new StreamType(stream, type));

	}

	@Override
	public void put(ContentEvent event) {

		for (int i = 0; i < streams.size(); i++) {

			switch (streams.get(i).getType()) {
			case SHUFFLE:
				S4Event s4event = new S4Event(event);
				s4event.setStreamId(streams.get(i).getStream().getName());
				if(getParallelism() == 1) {
					s4event.setKey("0");
				}else {
					s4event.setKey(Integer.toString(shuffleCounter));
				}
				streams.get(i).getStream().put(s4event);
				shuffleCounter++;
				 if (shuffleCounter >= (getParallelism())) {
					shuffleCounter = 0;
				}
				
				break;

			case GROUP_BY_KEY:
				S4Event s4event1 = new S4Event(event);
				s4event1.setStreamId(streams.get(i).getStream().getName());
				HashCodeBuilder hb = new HashCodeBuilder();
				hb.append(event.getKey());
				String key = Integer.toString(hb.build() % getParallelism());
				s4event1.setKey(key);
				streams.get(i).getStream().put(s4event1);
				break;
				
			case BROADCAST:
				for (int p = 0; p < this.getParallelism(); p++) {
					S4Event s4event2 = new S4Event(event);
					s4event2.setStreamId(streams.get(i).getStream().getName());
					s4event2.setKey(Integer.toString(p));
					streams.get(i).getStream().put(s4event2);
				}
				break;

			default:
				break;
			}

			
		}

	}

	/**
	 * Subclass for definig stream connection type
	 * @author severien
	 *
	 */
	class StreamType {
		org.apache.s4.core.Stream<S4Event> stream;
		int type;

		public StreamType(org.apache.s4.core.Stream<S4Event> s, int t) {
			this.stream = s;
			this.type = t;
		}

		public org.apache.s4.core.Stream<S4Event> getStream() {
			return stream;
		}

		public void setStream(org.apache.s4.core.Stream<S4Event> stream) {
			this.stream = stream;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

	}
}
