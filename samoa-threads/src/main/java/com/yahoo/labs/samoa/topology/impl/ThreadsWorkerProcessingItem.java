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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsWorkerProcessingItem implements ProcessingItem {

	private Processor processor;
	private int threadIndex;
	
	public ThreadsWorkerProcessingItem(Processor processor, int threadIndex) {
		this.processor = processor;
		this.threadIndex = threadIndex;
	}
	
	public int getThreadIndex() {
		return this.threadIndex;
	}
	
	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		return null;
	}

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
		return null;
	}

	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
		return null;
	}

	@Override
	public int getParalellism() {
		return 0;
	}

	public void processEvent(ContentEvent event) {
		this.processor.process(event);
	}
}
