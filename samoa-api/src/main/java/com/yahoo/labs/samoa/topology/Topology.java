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

import java.util.HashSet;
import java.util.Set;

/**
 * Topology abstract class.
 * 
 */
public abstract class Topology {

    protected Set<Stream> streams;
    protected Set<IProcessingItem> processingItems;
    private String task; // TODO: check if task is needed here

    protected Topology() {
        streams = new HashSet<Stream>();
        processingItems = new HashSet<IProcessingItem>();
    }

    /**
     * Add processing item to topology.
     * 
     * @param procItem
     *            Processing item.
     */
    protected void addProcessingItem(IProcessingItem procItem) {
        addProcessingItem(procItem, 1);
    }

    /**
     * Add processing item to topology.
     * 
     * @param procItem
     *            Processing item.
     * @param parallelismHint
     *            Processing item parallelism level.
     */
    protected void addProcessingItem(IProcessingItem procItem, int parallelismHint) {
        this.processingItems.add(procItem);
    }

    /**
     * Add stream to topology.
     * 
     * @param stream
     */
    protected void addStream(Stream stream) {
        this.streams.add(stream);
    }

    /**
     * Sets evaluation task.
     * 
     * @param task
     */
    public void setEvaluationTask(String task) {
        this.task = task;
    }

    /**
     * Gets evaluation task.
     * 
     * @return
     */
    public String getEvaluationTask() {
        return task;
    }

    /**
     * Adds an EntrancePI to the topology.
     * 
     * @param epi
     */
    public void addEntrancePi(EntranceProcessingItem epi) {
        this.addProcessingItem(epi);
    }
}
