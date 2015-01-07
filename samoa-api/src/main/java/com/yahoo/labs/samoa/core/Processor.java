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
 * The Interface Processor.
 */
public interface Processor extends Serializable, Configurable {

    /**
     * Entry point for the {@link Processor} code. This method is called once for every event received.
     * 
     * @param event
     *            the event to be processed.
     * @return true if successful, false otherwise.
     */
    boolean process(ContentEvent event);

    /**
     * Initializes the Processor.
     * This method is called once after the topology is set up and before any call to the {@link process} method.
     * 
     * @param id
     *            the identifier of the processor.
     */
    void onCreate(int id);

    /**
     * Creates a copy of a processor.
     * This method is used to instantiate multiple instances of the same {@link Processsor}.
     * 
     * @param processor
     *            the processor to be copied.
     * 
     * @return a new instance of the {@link Processor}.
     * */
    Processor newProcessor(Processor processor); // FIXME there should be no need for the processor as a parameter
    // TODO can we substitute this with Cloneable?
}
