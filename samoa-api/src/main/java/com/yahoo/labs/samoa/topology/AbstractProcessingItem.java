package com.yahoo.labs.samoa.topology;

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

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.utils.PartitioningScheme;

/**
 * Abstract ProcessingItem
 * 
 * Helper for implementation of ProcessingItem. It has basic information
 * for a ProcessingItem: name, parallelismLevel and a processor.
 * Subclass of this class needs to implement {@link #addInputStream(Stream, PartitioningScheme)}.
 * 
 * @author Anh Thu Vu
 *
 */
public abstract class AbstractProcessingItem implements ProcessingItem {
	private String name;
	private int parallelism;
	private Processor processor;
	
	/*
	 * Constructor
	 */
	public AbstractProcessingItem() {
		this(null);
	}
	public AbstractProcessingItem(Processor processor) {
		this(processor,1);
	}
	public AbstractProcessingItem(Processor processor, int parallelism) {
		this.processor = processor;
		this.parallelism = parallelism;
	}
	
	/*
	 * Processor
	 */
	/**
	 * Set the processor for this ProcessingItem
	 * @param processor
	 * 			the processor
	 */
	protected void setProcessor(Processor processor) {
		this.processor = processor;
	}
	
	/**
	 * Get the processor of this ProcessingItem
	 * @return the processor
	 */
	public Processor getProcessor() {
		return this.processor;
	}
	
	/*
	 * Parallelism 
	 */
	/**
	 * Set the parallelism factor of this ProcessingItem
	 * @param parallelism
	 */
	protected void setParallelism(int parallelism) {
		this.parallelism = parallelism;
	}
	
	/**
	 * Get the parallelism factor of this ProcessingItem
	 * @return the parallelism factor
	 */
	@Override
	public int getParallelism() {
		return this.parallelism;
	}
	
	/*
	 * Name/ID
	 */
	/**
	 * Set the name (or ID) of this ProcessingItem
	 * @param name
	 * 			the name/ID
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name (or ID) of this ProcessingItem
	 * @return the name/ID
	 */
	public String getName() {
		return this.name;
	}
	
	/*
	 * Add input streams
	 */
	/**
	 * Add an input stream to this ProcessingItem
	 * 
	 * @param inputStream
	 * 			the input stream to add
	 * @param scheme
	 * 			partitioning scheme associated with this ProcessingItem and the input stream
	 * @return this ProcessingItem
	 */
	protected abstract ProcessingItem addInputStream(Stream inputStream, PartitioningScheme scheme);

	/**
	 * Add an input stream to this ProcessingItem with SHUFFLE scheme
	 * 
	 * @param inputStream
	 * 			the input stream
	 * @return this ProcessingItem
	 */
    public ProcessingItem connectInputShuffleStream(Stream inputStream) {
    	return this.addInputStream(inputStream, PartitioningScheme.SHUFFLE);
    }

    /**
	 * Add an input stream to this ProcessingItem with GROUP_BY_KEY scheme
	 * 
	 * @param inputStream
	 * 			the input stream
	 * @return this ProcessingItem
	 */
    public ProcessingItem connectInputKeyStream(Stream inputStream) {
    	return this.addInputStream(inputStream, PartitioningScheme.GROUP_BY_KEY);
    }

    /**
	 * Add an input stream to this ProcessingItem with BROADCAST scheme
	 * 
	 * @param inputStream
	 * 			the input stream
	 * @return this ProcessingItem
	 */
    public ProcessingItem connectInputAllStream(Stream inputStream) {
    	return this.addInputStream(inputStream, PartitioningScheme.BROADCAST);
    }
}
