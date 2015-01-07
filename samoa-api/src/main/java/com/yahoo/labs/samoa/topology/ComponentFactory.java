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

import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;

/**
 * ComponentFactory interface. Provides platform specific components.
 */
public interface ComponentFactory {

    /**
     * Creates a platform specific processing item with the specified processor.
     * 
     * @param processor
     *            contains the logic for this processing item.
     * @return ProcessingItem
     */
    public ProcessingItem createPi(Processor processor);

    /**
     * Creates a platform specific processing item with the specified processor. Additionally sets the parallelism level.
     * 
     * @param processor
     *            contains the logic for this processing item.
     * @param parallelism
     *            defines the amount of instances of this processing item will be created.
     * @return ProcessingItem
     */
    public ProcessingItem createPi(Processor processor, int parallelism);

    /**
     * Creates a platform specific processing item with the specified processor that is the entrance point in the topology. This processing item can either
     * generate a stream of data or connect to an external stream of data.
     * 
     * @param entranceProcessor
     *            contains the logic for this processing item.
     * @return EntranceProcessingItem
     */
    public EntranceProcessingItem createEntrancePi(EntranceProcessor entranceProcessor);

    /**
     * Creates a platform specific stream.
     * 
     * @param sourcePi
     *            source processing item which will provide the events for this stream.
     * @return Stream
     */
    public Stream createStream(IProcessingItem sourcePi);

    /**
     * Creates a platform specific topology.
     * 
     * @param topoName
     *            Topology name.
     * @return Topology
     */
    public Topology createTopology(String topoName);
}
