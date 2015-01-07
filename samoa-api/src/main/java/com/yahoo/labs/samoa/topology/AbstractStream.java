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

import com.yahoo.labs.samoa.core.ContentEvent;

/**
 * Abstract Stream
 * 
 * Helper for implementation of Stream. It has basic information
 * for a Stream: streamID and source ProcessingItem.
 * Subclass of this class needs to implement {@link #put(ContentEvent)}.
 * 
 * @author Anh Thu Vu
 *
 */

public abstract class AbstractStream implements Stream {
	private String streamID;
	private IProcessingItem sourcePi;
	private int batchSize;
 
	/*
	 * Constructor
	 */
	public AbstractStream() {
		this(null);
	}
	public AbstractStream(IProcessingItem sourcePi) {
		this.sourcePi = sourcePi;
		this.batchSize = 1;
	}
	
	/**
	 * Get source processing item of this stream
	 * @return
	 */
	public IProcessingItem getSourceProcessingItem() {
		return this.sourcePi;
	}

    /*
     * Process event
     */
    @Override
    /**
     * Send a ContentEvent
     * @param event
     * 			the ContentEvent to be sent
     */
    public abstract void put(ContentEvent event);

    /*
     * Stream name
     */
    /**
     * Get name (ID) of this stream
     * @return the name (ID)
     */
    @Override
    public String getStreamId() {
    	return this.streamID;
    }
    
    /**
     * Set the name (ID) of this stream
     * @param streamID
     * 			the name (ID)
     */
    public void setStreamId (String streamID) {
    	this.streamID = streamID;
    }
  
    /*
     * Batch size
     */
    /**
     * Set suggested batch size
     *
     * @param batchSize
     * the suggested batch size
     *
     */
    @Override
    public void setBatchSize(int batchSize) {
    	this.batchSize = batchSize;
    }

    /**
     * Get suggested batch size
     *
     * @return the suggested batch size
     */
    public int getBatchSize() {
    	return this.batchSize;
    }
}
