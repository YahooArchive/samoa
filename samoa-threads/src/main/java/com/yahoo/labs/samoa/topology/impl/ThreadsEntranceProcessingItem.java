package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

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
