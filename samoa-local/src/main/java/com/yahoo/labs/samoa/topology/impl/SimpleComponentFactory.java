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

import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

/**
 *
 * @author abifet
 */
public class SimpleComponentFactory implements ComponentFactory{

    public ProcessingItem createPi(Processor processor, int paralellism) {
        SimpleProcessingItem pi = new SimpleProcessingItem(processor,paralellism);
        //System.out.println("createPi"+paralellism+" "+processor);
        return pi;
    }

    public ProcessingItem createPi(Processor processor) {
       return this.createPi(processor,1);
    }

    public EntranceProcessingItem createEntrancePi(Processor processor, TopologyStarter starter) {
        SimpleEntranceProcessingItem pi = new SimpleEntranceProcessingItem(processor, starter);
        return pi;
    }

    public Stream createStream(IProcessingItem sourcePi) {
        SimpleStream stream = new SimpleStream(sourcePi);
        //System.out.println("createStream "+sourcePi);
        return stream;
    }

    public Topology createTopology(String topoName) {
        return new SimpleTopology(topoName);
    }
    
}
