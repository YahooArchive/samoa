package com.yahoo.labs.samoa.topology.impl;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

public class ThreadsProcessingItem implements ProcessingItem {
	
	private Processor processor;
	private int parallelismHint;
	private List<ThreadsWorkerProcessingItem> listWorkerPi;
	private int offset;
	
	public ThreadsProcessingItem(Processor processor, int parallelismHint) {
		this.processor = processor;
		this.parallelismHint = parallelismHint;
		this.offset = (int) (Math.random()*ThreadsEngine.getNumberThreads());
	}

	@Override
	public Processor getProcessor() {
		return processor;
	}

	private ProcessingItem addInputStream(Stream inputStream, EventAllocationType type) {
		ThreadsStream stream = (ThreadsStream) inputStream;
		stream.addDestination(this, this.parallelismHint, type);
		return this;
	}
	
	@Override
	public ProcessingItem connectInputShuffleStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.SHUFFLE);
	}

	@Override
	public ProcessingItem connectInputKeyStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.GROUP_BY_KEY);
	}

	@Override
	public ProcessingItem connectInputAllStream(Stream inputStream) {
		return this.addInputStream(inputStream, EventAllocationType.BROADCAST);
	}

	@Override
	public int getParalellism() {
		return this.parallelismHint;
	}
	
	public void setupWorkers() {
		this.listWorkerPi = new ArrayList<ThreadsWorkerProcessingItem>(parallelismHint);
		for (int i=0; i<this.parallelismHint; i++) {
			Processor newProcessor = this.processor.newProcessor(this.processor);
			newProcessor.onCreate(i + 1);
			this.listWorkerPi.add(new ThreadsWorkerProcessingItem(newProcessor, this.offset + i));
		}
	}
	
	public void processEvent(ContentEvent event, int counter) {
		ThreadsWorkerProcessingItem workerPi = this.listWorkerPi.get(counter);
		ThreadsEventRunnable runnable = new ThreadsEventRunnable(workerPi, event);
		ThreadsEngine.getThreadWithIndex(workerPi.getThreadIndex()).submit(runnable);
	}

}
