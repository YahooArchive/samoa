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
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;

/**
 * Builder class that creates topology components and assemble them together.
 * 
 */
public class TopologyBuilder {

    // TODO:
    // Possible options:
    // 1. we may convert this as interface and platform dependent builder will inherit this method
    // 2. refactor by combining TopologyBuilder, ComponentFactory and Topology
    // -ve -> fat class where it has capabilities to instantiate specific component and connecting them
    // +ve -> easy abstraction for SAMOA developer "you just implement your builder logic here!"
    private ComponentFactory componentFactory;
    private Topology topology;
    private Map<Processor, IProcessingItem> mapProcessorToProcessingItem;

    // TODO: refactor, temporary constructor used by Storm code
    public TopologyBuilder() {
        // TODO: initialize _componentFactory using dynamic binding
        // for now, use StormComponentFactory
        // should the factory be Singleton (?)
        // ans: at the moment, no, i.e. each builder will has its associated factory!
        // and the factory will be instantiated using dynamic binding
        // this.componentFactory = new StormComponentFactory();
    }

    // TODO: refactor, temporary constructor used by S4 code
    public TopologyBuilder(ComponentFactory theFactory) {
        this.componentFactory = theFactory;
    }

    /**
     * Initiates topology with a specific name.
     * 
     * @param topologyName
     */
    public void initTopology(String topologyName) {
    	this.initTopology(topologyName, 0);
    }
    
    /**
     * Initiates topology with a specific name and a delay between consecutive instances.
     * 
     * @param topologyName
     * @param delay
     * 			delay between injections of two instances from source (in milliseconds)
     */
    public void initTopology(String topologyName, int delay) {
        if (this.topology != null) {
            // TODO: possible refactor this code later
            System.out.println("Topology has been initialized before!");
            return;
        }
        this.topology = componentFactory.createTopology(topologyName);
    }

    /**
     * Returns the platform specific topology.
     * 
     * @return
     */
    public Topology build() {
        return topology;
    }

    public ProcessingItem addProcessor(Processor processor, int parallelism) {
        ProcessingItem pi = createPi(processor, parallelism);
        if (this.mapProcessorToProcessingItem == null)
            this.mapProcessorToProcessingItem = new HashMap<Processor, IProcessingItem>();
        this.mapProcessorToProcessingItem.put(processor, pi);
        return pi;
    }

    public ProcessingItem addProcessor(Processor processor) {
        return addProcessor(processor, 1);
    }

    public ProcessingItem connectInputShuffleStream(Stream inputStream, Processor processor) {
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        return pi.connectInputShuffleStream(inputStream);
    }

    public ProcessingItem connectInputKeyStream(Stream inputStream, Processor processor) {
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        return pi.connectInputKeyStream(inputStream);
    }

    public ProcessingItem connectInputAllStream(Stream inputStream, Processor processor) {
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        return pi.connectInputAllStream(inputStream);
    }

    public Stream createInputShuffleStream(Processor processor, Processor dest) {
        Stream inputStream = this.createStream(dest);
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        pi.connectInputShuffleStream(inputStream);
        return inputStream;
    }

    public Stream createInputKeyStream(Processor processor, Processor dest) {
        Stream inputStream = this.createStream(dest);
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        pi.connectInputKeyStream(inputStream);
        return inputStream;
    }

    public Stream createInputAllStream(Processor processor, Processor dest) {
        Stream inputStream = this.createStream(dest);
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to connect to null PI");
        pi.connectInputAllStream(inputStream);
        return inputStream;
    }

    public Stream createStream(Processor processor) {
        IProcessingItem pi = mapProcessorToProcessingItem.get(processor);
        Stream ret = null;
        Preconditions.checkNotNull(pi, "Trying to create stream from null PI");
        ret = this.createStream(pi);
        if (pi instanceof EntranceProcessingItem)
            ((EntranceProcessingItem) pi).setOutputStream(ret);
        return ret;
    }

    public EntranceProcessingItem addEntranceProcessor(EntranceProcessor entranceProcessor) {
        EntranceProcessingItem pi = createEntrancePi(entranceProcessor);
        if (this.mapProcessorToProcessingItem == null)
            this.mapProcessorToProcessingItem = new HashMap<Processor, IProcessingItem>();
        mapProcessorToProcessingItem.put(entranceProcessor, pi);
        return pi;
    }

    public ProcessingItem getProcessingItem(Processor processor) {
        ProcessingItem pi = (ProcessingItem) mapProcessorToProcessingItem.get(processor);
        Preconditions.checkNotNull(pi, "Trying to retrieve null PI");
        return pi;
    }

    /**
     * Creates a processing item with a specific processor and paralellism level of 1.
     * 
     * @param processor
     * @return ProcessingItem
     */
    @SuppressWarnings("unused")
    private ProcessingItem createPi(Processor processor) {
        return createPi(processor, 1);
    }

    /**
     * Creates a processing item with a specific processor and paralellism level.
     * 
     * @param processor
     * @param parallelism
     * @return ProcessingItem
     */
    private ProcessingItem createPi(Processor processor, int parallelism) {
        ProcessingItem pi = this.componentFactory.createPi(processor, parallelism);
        this.topology.addProcessingItem(pi, parallelism);
        return pi;
    }

    /**
     * Creates a platform specific entrance processing item.
     * 
     * @param processor
     * @return
     */
    private EntranceProcessingItem createEntrancePi(EntranceProcessor processor) {
        EntranceProcessingItem epi = this.componentFactory.createEntrancePi(processor);
        this.topology.addEntranceProcessingItem(epi);
        if (this.mapProcessorToProcessingItem == null)
            this.mapProcessorToProcessingItem = new HashMap<Processor, IProcessingItem>();
        this.mapProcessorToProcessingItem.put(processor, epi);
        return epi;
    }

    /**
     * Creates a platform specific stream.
     * 
     * @param sourcePi
     *            source processing item.
     * @return
     */
    private Stream createStream(IProcessingItem sourcePi) {
        Stream stream = this.componentFactory.createStream(sourcePi);
        this.topology.addStream(stream);
        return stream;
    }
}
