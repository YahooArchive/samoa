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
import java.util.UUID;

import org.apache.s4.base.KeyFinder;
import org.apache.s4.core.App;
import org.apache.s4.core.ProcessingElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * S4 Platform platform specific processing item, inherits from S4 ProcessinElemnt.
 * 
 * @author severien
 *
 */
public class S4ProcessingItem extends ProcessingElement implements
		ProcessingItem {

	public static final Logger logger = LoggerFactory
			.getLogger(S4ProcessingItem.class);

	private Processor processor;
	private int paralellismLevel;
	private S4DoTask app;

	private static final String NAME="PROCESSING-ITEM-";
	private static int OBJ_COUNTER=0;
	
	/**
	 * Constructor of S4 ProcessingItem.
	 * 
	 * @param app : S4 application
	 */
	public S4ProcessingItem(App app) {
		super(app);
		super.setName(NAME+OBJ_COUNTER);
		OBJ_COUNTER++;
		this.app = (S4DoTask) app;
		this.paralellismLevel = 1;
	}

	@Override
	public String getName() {
		return super.getName();
	}
	
	/**
	 * Gets processing item paralellism level.
	 * 
	 * @return int
	 */
	public int getParalellismLevel() {
		return paralellismLevel;
	}

	/**
	 * Sets processing item paralellism level.
	 * 
	 * @param paralellismLevel
	 */
	public void setParalellismLevel(int paralellismLevel) {
		this.paralellismLevel = paralellismLevel;
	}

	/**
	 * onEvent method.
	 * 
	 * @param event
	 */
	public void onEvent(S4Event event) {
		if (processor.process(event.getContentEvent()) == true) {
			close();
		}
	}

	/**
	 * Sets S4 processing item processor.
	 * 
	 * @param processor
	 */
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	// Methods from ProcessingItem
	@Override
	public Processor getProcessor() {
		return processor;
	}

	/**
	 * KeyFinder sets the keys for a specific event.
	 * 
	 * @return KeyFinder
	 */
	private KeyFinder<S4Event> getKeyFinder() {
		KeyFinder<S4Event> keyFinder = new KeyFinder<S4Event>() {
			@Override
			public List<String> get(S4Event s4event) {
				List<String> results = new ArrayList<String>();
				results.add(s4event.getKey());
				return results;
			}
		};

		return keyFinder;
	}
	
	
	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {

		S4Stream stream = (S4Stream) inputStream;
		stream.setParallelism(this.paralellismLevel);
		stream.addStream(inputStream.getStreamId(),
				getKeyFinder(), this, S4Stream.BROADCAST);
		return this;
	}

	
	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {

		S4Stream stream = (S4Stream) inputStream;
		stream.setParallelism(this.paralellismLevel);
		stream.addStream(inputStream.getStreamId(),
				getKeyFinder(), this,S4Stream.GROUP_BY_KEY);

		return this;
	}
	
	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		S4Stream stream = (S4Stream) inputStream;
		stream.setParallelism(this.paralellismLevel);
		stream.addStream(inputStream.getStreamId(),
				getKeyFinder(), this,S4Stream.SHUFFLE);

		return this;
	}

	// Methods from ProcessingElement
	@Override
	protected void onCreate() {
		logger.debug("PE ID {}", getId());		
				if (this.processor != null) {
			this.processor = this.processor.newProcessor(this.processor);
			this.processor.onCreate(Integer.parseInt(getId()));
		}
	}

	@Override
	protected void onRemove() {
		// do nothing
	}

	@Override
	public int getParallelism() {
		return this.paralellismLevel;
	}
}
