package com.yahoo.labs.samoa.topology.impl;

import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

public class ThreadsComponentFactory implements ComponentFactory {

	@Override
	public ProcessingItem createPi(Processor processor) {
		return this.createPi(processor, 1);
	}

	@Override
	public ProcessingItem createPi(Processor processor, int paralellism) {
		return new ThreadsProcessingItem(processor, paralellism);
	}

	@Override
	public EntranceProcessingItem createEntrancePi(EntranceProcessor entranceProcessor) {
		return new ThreadsEntranceProcessingItem(entranceProcessor);
	}

	@Override
	public Stream createStream(IProcessingItem sourcePi) {
		return new ThreadsStream(sourcePi);
	}

	@Override
	public Topology createTopology(String topoName) {
		return new ThreadsTopology(topoName);
	}

}
