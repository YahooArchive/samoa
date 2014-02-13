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
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsEntranceProcessingItem implements EntranceProcessingItem {
	
	private EntranceProcessor processor;
	private Stream outputStream;
	
	public ThreadsEntranceProcessingItem(EntranceProcessor processor) {
		this.processor = processor;
	}

	@Override
	public EntranceProcessor getProcessor() {
		return processor;
	}

	@Override
	public EntranceProcessingItem setOutputStream(Stream stream) {
		if (this.outputStream != null)
            throw new IllegalStateException("Output stream for an EntrancePI should be initialized only once");
        this.outputStream = stream;
        return this;
	}
	
	public boolean injectNextEvent() {
		boolean hasNext = this.processor.hasNext();
    	ContentEvent nextEvent = this.processor.nextEvent();
        outputStream.put(nextEvent);
        return hasNext;
	}

}
