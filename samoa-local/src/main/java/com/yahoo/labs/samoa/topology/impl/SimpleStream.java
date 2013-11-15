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

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

/**
 *
 * @author abifet
 */
class SimpleStream implements Stream {

    public static final int SHUFFLE = 0;
    public static final int GROUP_BY_KEY = 1;
    public static final int BROADCAST = 2;
    private IProcessingItem sourcePi;
    private List<IProcessingItem> listProcessingItem;
    private List<Integer> listTypeStream;
    private int processingItemParalellism;
    private int shuffleCounter;

    public int getParalellism() {
        return processingItemParalellism;
    }

    public void setParalellism(int paralellism) {
        this.processingItemParalellism = paralellism;
    }

    SimpleStream(IProcessingItem sourcePi) {
        this.sourcePi = sourcePi;
        this.listProcessingItem = new LinkedList<IProcessingItem>();
        this.listTypeStream = new LinkedList<Integer>();
    }

    public void put(ContentEvent event) {
        int type;
        SimpleProcessingItem pi;
        for (int i = 0; i < this.listProcessingItem.size(); i++) {
            type = this.listTypeStream.get(i);
            //System.out.println("PUT Event"+type);
            pi = (SimpleProcessingItem) this.listProcessingItem.get(i);
            switch (type) {
                case SHUFFLE:
                    shuffleCounter++;
                    if (shuffleCounter >= (getParalellism())) {
                        shuffleCounter = 0;
                    }
                    //pi = ((SimpleProcessingItem) this.listProcessingItem.get(i)).getProcessingItem(shuffleCounter);
                    //pi.getProcessor().process(event);
                   
                    pi.processEvent(event,shuffleCounter);
                    break;
                case GROUP_BY_KEY:
                    HashCodeBuilder hb = new HashCodeBuilder();
                    hb.append(event.getKey());
                    int key = hb.build() % getParalellism();
                    //pi = ((SimpleProcessingItem) this.listProcessingItem.get(i)).getProcessingItem(key);
                    //pi.getProcessor().process(event);
                    pi.processEvent(event, key);
                    break;
                case BROADCAST:
                    for (int p = 0; p < this.getParalellism(); p++) {
                        //pi = ((SimpleProcessingItem) this.listProcessingItem.get(i)).getProcessingItem(p);
                        //pi.getProcessor().process(event);
                        pi.processEvent(event, p);
                    }
                    break;
            }
        }
    }

    public String getStreamId() {
        return null;
    }

    public void add(IProcessingItem destinationPi, int type, int paralellism) {


        this.listTypeStream.add(type);
        this.setParalellism(paralellism);
        //System.out.println("STREAM Added "+destinationPi.toString()+" type "+ type+"paral "+paralellism);
        this.listProcessingItem.add(destinationPi);
        /*IProcessingItem[] arrayPi = new IProcessingItem[paralellism];
        for (int j = 0; j < paralellism; j++) {
            arrayPi[j] = ((SimpleProcessingItem) destinationPi).copy();
            arrayPi[j].getProcessor().onCreate(0);
        }
        this.listProcessingItem.add(arrayPi);
        */
    }
}
