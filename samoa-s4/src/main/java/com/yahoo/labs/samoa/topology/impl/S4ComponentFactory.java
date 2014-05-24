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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * S4 Platform Component Factory
 * 
 * @author severien
 * 
 */
public class S4ComponentFactory implements ComponentFactory {

    public static final Logger logger = LoggerFactory.getLogger(S4ComponentFactory.class);
    protected S4DoTask app;

    @Override
    public ProcessingItem createPi(Processor processor, int paralellism) {
        S4ProcessingItem processingItem = new S4ProcessingItem(app);
        // TODO refactor how to set the paralellism level
        processingItem.setParalellismLevel(paralellism);
        processingItem.setProcessor(processor);

        return processingItem;
    }

    @Override
    public ProcessingItem createPi(Processor processor) {
        return this.createPi(processor, 1);
    }

    @Override
    public EntranceProcessingItem createEntrancePi(EntranceProcessor entranceProcessor) {
        // TODO Create source Entry processing item that connects to an external stream
        S4EntranceProcessingItem entrancePi = new S4EntranceProcessingItem(entranceProcessor, app);
        entrancePi.setParallelism(1); // FIXME should not be set to 1 statically
        return entrancePi;
    }

    @Override
    public Stream createStream(IProcessingItem sourcePi) {
        S4Stream aStream = new S4Stream(app);
        return aStream;
    }

    @Override
    public Topology createTopology(String topoName) {
        return new S4Topology(topoName);
    }

    /**
     * Initialization method.
     * 
     * @param evalTask
     */
    public void init(String evalTask) {
        // Task is initiated in the DoTaskApp
    }

    /**
     * Sets S4 application.
     * 
     * @param app
     */
    public void setApp(S4DoTask app) {
        this.app = app;
    }
}
