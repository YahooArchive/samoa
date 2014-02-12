package com.yahoo.labs.samoa.topology.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

public class ThreadsStream implements Stream {
	private List<DestinationPIWrapper> listDestination;
	private int counter = 0;
	private int maxCounter = 1;
	
	public ThreadsStream(IProcessingItem sourcePi) {
		listDestination = new ArrayList<DestinationPIWrapper>();
	}
	
	public void addDestination(IProcessingItem pi, int parallelismHint, EventAllocationType type) {
		listDestination.add(new DestinationPIWrapper(pi, parallelismHint, type));
		maxCounter *= parallelismHint;
	}

	@Override
	public synchronized void put(ContentEvent event) {
        DestinationPIWrapper destination;
        ThreadsProcessingItem pi;
        for (int i = 0; i < this.listDestination.size(); i++) {
            destination = this.listDestination.get(i);
            pi = (ThreadsProcessingItem) destination.getProcessingItem();
            counter++;
            if (counter >= maxCounter) counter = 0;
            switch (destination.getEventAllocationType()) {
            case SHUFFLE:
                pi.processEvent(event, counter%destination.getParallelism());
                break;
            case GROUP_BY_KEY:
                HashCodeBuilder hb = new HashCodeBuilder();
                hb.append(event.getKey());
                int key = hb.build() % destination.getParallelism();
                pi.processEvent(event, key);
                break;
            case BROADCAST:
                for (int p = 0; p < destination.getParallelism(); p++) {
                    pi.processEvent(event, p);
                }
                break;
            }
        }
	}

	@Override
	public String getStreamId() {
		return null;
	}

}
