package com.yahoo.labs.samoa.examples;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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
import com.yahoo.labs.samoa.core.Processor;

/**
 * Example {@link Processor} that simply prints the received events to standard output.
 */
public class HelloWorldDestinationProcessor implements Processor {

    private static final long serialVersionUID = -6042613438148776446L;
    private int processorId;

    @Override
    public boolean process(ContentEvent event) {
        System.out.println(processorId + ": " + event);
        return true;
    }

    @Override
    public void onCreate(int id) {
        this.processorId = id;
    }

    @Override
    public Processor newProcessor(Processor p) {
        return new HelloWorldDestinationProcessor();
    }
}
