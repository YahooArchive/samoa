package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.ProcessingItem;

public class ThreadsEventRunnable implements Runnable {

	private ProcessingItem pi;
	private ContentEvent event;
	
	public ThreadsEventRunnable(ProcessingItem pi, ContentEvent event) {
		this.pi = pi;
		this.event = event;
	}
	
	@Override
	public void run() {
		ThreadsWorkerProcessingItem workerPi = (ThreadsWorkerProcessingItem) pi;
		workerPi.processEvent(event);
	}

}
