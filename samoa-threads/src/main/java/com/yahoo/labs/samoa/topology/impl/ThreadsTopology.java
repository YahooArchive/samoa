package com.yahoo.labs.samoa.topology.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Topology;

public class ThreadsTopology extends Topology {
	private static final Logger logger = LoggerFactory.getLogger(ThreadsTopology.class);
	
	public String topologyName;
    private ThreadsEntranceProcessingItem entrancePi;
    
    private void setupWorkers() {
    	for (IProcessingItem pi:this.processingItems) {
    		if (pi instanceof ThreadsProcessingItem) {
    			((ThreadsProcessingItem) pi).setupWorkers();
    		}
    	}
    }

    public void run(int delay) {
    	while(entrancePi.injectNextEvent()) {
    		if (delay > 0) {
    			try {
    				Thread.sleep(delay);
    			} catch (InterruptedException e) {
    				logger.error("Topology was interrupted while sleeping.");
    			}
    		}
    	}
    }
    
    public void start(int delay) {
        this.setupWorkers();
    	this.run(delay);
    }

    public ThreadsTopology(String topoName) {
        this.topologyName = topoName;
    }

    public EntranceProcessingItem getEntranceProcessingItem() {
        return entrancePi;
    }

    @Override
    public void addEntrancePi(EntranceProcessingItem epi) {
        this.entrancePi = (ThreadsEntranceProcessingItem) epi;
        this.addProcessingItem(epi);
    }
}
