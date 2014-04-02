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

import java.util.Random;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.Stream;

public class HelloWorldSourceProcessor implements EntranceProcessor {

    /**
     *
     */
    private static final long serialVersionUID = 6212296305865604747L;

    private Random rnd;
//    private Stream helloWorldStream;

    private final long maxInst;
    private long count;

    public HelloWorldSourceProcessor(long maxInst) {
        this.maxInst = maxInst;
    }

    @Override
    public boolean process(ContentEvent event) {
        // do nothing, API will be refined further
        return false;
    }

    @Override
    public void onCreate(int id) {
        rnd = new Random(id);
    }

    @Override
    public Processor newProcessor(Processor p) {
        HelloWorldSourceProcessor hwsp = (HelloWorldSourceProcessor) p;
        return new HelloWorldSourceProcessor(hwsp.maxInst);
    }

//    public void setHelloWorldStream(Stream hwStream) {
//        this.helloWorldStream = hwStream;
//    }

    // public void sendInstance() {
    // int count = 0;
    //
    // while (count < maxInst) {
    // this.helloWorldStream.put(new HelloWorldContentEvent(rnd.nextInt(), false));
    // count++;
    // }
    //
    // this.helloWorldStream.put(new HelloWorldContentEvent(-1, true));
    // }

    @Override
    public boolean hasNext() {
        return count < maxInst;
    }

    @Override
    public ContentEvent nextEvent() {
        count++;
        return new HelloWorldContentEvent(rnd.nextInt(), false);
    }

    // public static class HelloWorldTopologyStarter implements TopologyStarter {
    //
    // /**
    // *
    // */
    // private static final long serialVersionUID = 5445314667316145715L;
    //
    // private final HelloWorldSourceProcessor hwsp;
    //
    // public HelloWorldTopologyStarter(HelloWorldSourceProcessor hwsp) {
    // this.hwsp = hwsp;
    // }
    //
    // @Override
    // public void start() {
    // this.hwsp.sendInstance();
    // }
    // }
}
