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

import java.util.List;
import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 *
 * @author abifet
 */
class SimpleProcessingItem implements ProcessingItem {

    public static final int SHUFFLE = 0;
    public static final int GROUP_BY_KEY = 1;
    public static final int BROADCAST = 2;
    protected Processor processor;
    private int processingItemParalellism;
    private IProcessingItem[] arrayProcessingItem;

    public IProcessingItem getProcessingItem(int i) {
        return arrayProcessingItem[i];
    }

    SimpleProcessingItem(Processor processor, int paralellism) {
        this.processor = processor;
        this.processingItemParalellism = paralellism;
    }

    public ProcessingItem connectInputShuffleStream(Stream inputStream) {
        SimpleStream stream = (SimpleStream) inputStream;
        stream.add(this, SHUFFLE, this.processingItemParalellism);
        return this;
    }

    public ProcessingItem connectInputKeyStream(Stream inputStream) {
        SimpleStream stream = (SimpleStream) inputStream;
        stream.add(this, GROUP_BY_KEY, this.processingItemParalellism);
        return this;
    }

    public ProcessingItem connectInputAllStream(Stream inputStream) {
        SimpleStream stream = (SimpleStream) inputStream;
        stream.add(this, BROADCAST, this.processingItemParalellism);
        return this;
    }

    public int getParalellism() {
        return processingItemParalellism;
    }

    public Processor getProcessor() {
        return this.processor;
    }

    public SimpleProcessingItem copy() {
        SimpleProcessingItem ret = new SimpleProcessingItem(this.processor.newProcessor(this.processor), 0); // this.getParalellism());
        return ret;
    }

    public void processEvent(ContentEvent event, int counter) {
        int paralellism = this.getParalellism();
        if (this.arrayProcessingItem == null && paralellism > 0) {
            //Init processing elements, the first time they are needed
            this.arrayProcessingItem = new IProcessingItem[paralellism];
            for (int j = 0; j < paralellism; j++) {
                arrayProcessingItem[j] = this.copy();
                arrayProcessingItem[j].getProcessor().onCreate(j);
                //System.out.println(j + " PROCESSOR create " + arrayProcessingItem[j].getProcessor());
            }
        }
        if (this.arrayProcessingItem != null) {
            this.getProcessingItem(counter).getProcessor().process(event);
        }
    }
}
