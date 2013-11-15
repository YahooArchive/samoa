/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;

/**
 *
 * @author abifet
 */
class SimpleEntranceProcessingItem implements EntranceProcessingItem{

    protected Processor processor;
    protected TopologyStarter topologyStarter;

    public TopologyStarter getTopologyStarter() {
        return topologyStarter;
    }
    
    public SimpleEntranceProcessingItem(Processor processor, TopologyStarter starter) {
        this.processor = processor;
        this.topologyStarter = starter;
    }

    public void put(Instance inst) {
        // do nothing, we not need this method
    }

    public Processor getProcessor() {
        return this.processor;
    }
    
}
