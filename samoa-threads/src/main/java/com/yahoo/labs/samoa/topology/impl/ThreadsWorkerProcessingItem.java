package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

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
