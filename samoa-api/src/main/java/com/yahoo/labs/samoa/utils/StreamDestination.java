package com.yahoo.labs.samoa.utils;

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

import com.yahoo.labs.samoa.topology.IProcessingItem;

/**
 * Represents one destination for streams. It has the info of:
 * the ProcessingItem, parallelismHint, and partitioning scheme.
 * Usage:
 * - When ProcessingItem connects to a stream, it will pass 
 * a StreamDestination to the stream.
 * - Stream manages a set of StreamDestination.
 * - Used in single-threaded and multi-threaded local mode.
 * @author Anh Thu Vu
 *
 */
public class StreamDestination {
	private IProcessingItem pi;
	private int parallelism;
	private PartitioningScheme type;
	
	/*
	 * Constructor
	 */
	public StreamDestination(IProcessingItem pi, int parallelismHint, PartitioningScheme type) {
		this.pi = pi;
		this.parallelism = parallelismHint;
		this.type = type;
	}
	
	/*
	 * Getters
	 */
	public IProcessingItem getProcessingItem() {
		return this.pi;
	}
	
	public int getParallelism() {
		return this.parallelism;
	}
	
	public PartitioningScheme getPartitioningScheme() {
		return this.type;
	}

}
