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

import org.apache.s4.core.App;
import org.apache.s4.core.ProcessingElement;

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;

// TODO adapt this entrance processing item to connect to external streams so the application doesnt need to use an AdapterApp

public class S4EntranceProcessingItem extends ProcessingElement implements
		EntranceProcessingItem {

	private Processor processor;
	S4DoTask app;
	private int paralellism;

	/**
	 * Constructor of an S4 entrance processing item.
	 * 
	 * @param app
	 *            : S4 application
	 */
	public S4EntranceProcessingItem(App app) {
		super(app);
		this.app = (S4DoTask) app;
		// this.setSingleton(true);
	}

	public void setParalellism(int paralellism) {
		this.paralellism = paralellism;
	}

	public int getParalellism() {
		return this.paralellism;
	}

	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public void put(Instance inst) {
		// do nothing
		// may not needed
	}

	@Override
	protected void onCreate() {
		//was commented
		if (this.processor != null) {
			this.processor = this.processor.newProcessor(this.processor);
			this.processor.onCreate(Integer.parseInt(getId()));
		}
	}

	@Override
	protected void onRemove() {
		// do nothing

	}

	/**
	 * Sets the entrance processing item processor.
	 * 
	 * @param processor
	 */
	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}

}
