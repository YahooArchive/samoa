package com.yahoo.labs.samoa.learners.classifiers.rules.distributed;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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
import com.yahoo.labs.samoa.topology.Stream;

public class AMRResultCombinerProcessor implements Processor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3336289215851867940L;

	private int processorId;
	
	// SAMOA Stream
	private Stream resultStream;
	
	@Override
	public boolean process(ContentEvent event) {
		resultStream.put(event); // forward all events to destination
		return false;
	}

	@Override
	public void onCreate(int id) {
		this.processorId = id;
	}

	@Override
	public Processor newProcessor(Processor processor) {
		AMRResultCombinerProcessor oldP = (AMRResultCombinerProcessor)processor;
		AMRResultCombinerProcessor newP = new AMRResultCombinerProcessor();
		newP.resultStream = oldP.resultStream;
		return newP;
	}
	
	/*
	 * Stream
	 */
	public void setResultStream(Stream stream) {
		this.resultStream = stream;
	}
	public Stream getResultStream() {
		return this.resultStream;
	}
}
