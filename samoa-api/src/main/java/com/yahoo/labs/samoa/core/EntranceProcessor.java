package com.yahoo.labs.samoa.core;

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

import java.io.Serializable;

import com.github.javacliparser.Configurable;

/**
 * An EntranceProcessor is a specific kind of processor dedicated to providing events to inject in the topology. It can be connected to a single output stream.
 */
public interface EntranceProcessor extends Serializable, Configurable, Processor {

    /**
     * Initializes the Processor. This method is called once after the topology is set up and before any call to the {@link nextTuple} method.
     * 
     * @param the
     *            identifier of the processor.
     */
    public void onCreate(int id);
    
    /**
     * Checks whether the source stream is finished/exhausted.
     */
    public boolean isFinished();
    
    /**
     * Checks whether a new event is ready to be processed.
     * 
     * @return true if the EntranceProcessor is ready to provide the next event, false otherwise.
     */
    public boolean hasNext();

    /**
     * Provides the next tuple to be processed by the topology. This method is the entry point for external events into the topology.
     * 
     * @return the next event to be processed.
     */
    public ContentEvent nextEvent();

}
