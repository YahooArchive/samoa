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

import com.yahoo.labs.samoa.core.ContentEvent;

/**
 * Stream interface.
 *
 * @author severien
 *
 */
public interface Stream {
	
	/**
	 * Puts events into a platform specific data stream.
	 * 
	 * @param event
	 */
	public void put(ContentEvent event);
	
	/**
	 * Sets the stream id which is represented by a name.
	 * 
	 * @param stream
	 */
	//public void setStreamId(String stream);
	
	
	/**
	 * Gets stream id.
	 * 
	 * @return id
	 */
	public String getStreamId();
	
	/**
	 * Set batch size
	 *
	 * @param batchSize
	 *                  the suggested size for batching messages on this stream
	 */
	public void setBatchSize(int batchsize);
}