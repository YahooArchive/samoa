package com.yahoo.labs.samoa.topology;

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

/**
 * Processing item interface.
 * 
 * @author severien
 * 
 */
public interface ProcessingItem extends IProcessingItem {

	/**
	 * Connects this processing item in a round robin fashion. The events will
	 * be distributed evenly between the instantiated processing items.
	 * 
	 * @param inputStream
	 *            Stream to connect this processing item.
	 * @return ProcessingItem
	 */
	public ProcessingItem connectInputShuffleStream(Stream inputStream);

	/**
	 * Connects this processing item taking the event key into account. Events
	 * will be routed to the processing item according to the modulus of its key
	 * and the paralellism level. Ex.: key = 5 and paralellism = 2, 5 mod 2 = 1.
	 * Processing item responsible for 1 will receive this event.
	 * 
	 * @param inputStream
	 *            Stream to connect this processing item.
	 * @return ProcessingItem
	 */
	public ProcessingItem connectInputKeyStream(Stream inputStream);

	/**
	 * Connects this processing item to the stream in a broadcast fashion. All
	 * processing items of this type will receive copy of the original event.
	 * 
	 * @param inputStream
	 *            Stream to connect this processing item.
	 * @return ProcessingItem
	 */
	public ProcessingItem connectInputAllStream(Stream inputStream);


	/**
	 * Gets processing item parallelism level.
	 * 
	 * @return int
	 */
	public int getParallelism();
}
