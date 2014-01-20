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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.TopologyStarter;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;

class SimpleEntranceProcessingItem implements EntranceProcessingItem {

    protected EntranceProcessor entranceProcessor;
    protected TopologyStarter topologyStarter;
    protected SimpleStream outputStream;

    public TopologyStarter getTopologyStarter() {
        return topologyStarter;
    }

    public SimpleEntranceProcessingItem setOutputStream(SimpleStream stream) {
        if (this.outputStream != null)
            throw new IllegalStateException("Output stream for an EntrancePI can be initialized only once");
        this.outputStream = stream;
        return this;
    }

    public SimpleEntranceProcessingItem(EntranceProcessor processor, TopologyStarter starter) {
        this.entranceProcessor = processor;
        this.topologyStarter = starter;
    }

    @Override
    public EntranceProcessor getProcessor() {
        return this.entranceProcessor;
    }

    @Override
    public boolean inject(ContentEvent event) {
        outputStream.put(event);
        return false;
    }
}