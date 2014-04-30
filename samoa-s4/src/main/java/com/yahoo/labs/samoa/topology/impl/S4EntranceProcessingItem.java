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

import org.apache.s4.core.App;
import org.apache.s4.core.ProcessingElement;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;

// TODO adapt this entrance processing item to connect to external streams so the application doesnt need to use an AdapterApp

public class S4EntranceProcessingItem extends ProcessingElement implements EntranceProcessingItem {

    private EntranceProcessor entranceProcessor;
    // private S4DoTask app;
    private int parallelism;
    protected Stream outputStream;

    /**
     * Constructor of an S4 entrance processing item.
     * 
     * @param app
     *            : S4 application
     */
    public S4EntranceProcessingItem(EntranceProcessor entranceProcessor, App app) {
        super(app);
        this.entranceProcessor = entranceProcessor;
        // this.app = (S4DoTask) app;
        // this.setSingleton(true);
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getParallelism() {
        return this.parallelism;
    }

    @Override
    public EntranceProcessor getProcessor() {
        return this.entranceProcessor;
    }

    //
    // @Override
    // public void put(Instance inst) {
    // // do nothing
    // // may not needed
    // }

    @Override
    protected void onCreate() {
        // was commented
        if (this.entranceProcessor != null) {
            // TODO revisit if we need to change it to a clone() call
            this.entranceProcessor = (EntranceProcessor) this.entranceProcessor.newProcessor(this.entranceProcessor);
            this.entranceProcessor.onCreate(Integer.parseInt(getId()));
        }
    }

    @Override
    protected void onRemove() {
        // do nothing
    }

    //
    // /**
    // * Sets the entrance processing item processor.
    // *
    // * @param processor
    // */
    // public void setProcessor(Processor processor) {
    // this.entranceProcessor = processor;
    // }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public EntranceProcessingItem setOutputStream(Stream stream) {
        if (this.outputStream != null)
            throw new IllegalStateException("Output stream for an EntrancePI sohuld be initialized only once");
        this.outputStream = stream;
        return this;
    }

    public boolean injectNextEvent() {
        if (entranceProcessor.hasNext()) {
            ContentEvent nextEvent = this.entranceProcessor.nextEvent();
            outputStream.put(nextEvent);
            return entranceProcessor.hasNext();
        } else
            return false;
        // return !nextEvent.isLastEvent();
    }
}
