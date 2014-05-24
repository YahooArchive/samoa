//package com.yahoo.labs.samoa.streams;
//
///*
// * #%L
// * SAMOA
// * %%
// * Copyright (C) 2013 Yahoo! Inc.
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//import com.yahoo.labs.samoa.topology.Stream;
//
///**
// * Topology Starter is a wrapper of PrequentialSourceProcessor so that the Entrance PI could start the execution of the topology.
// * 
// */
//public class PrequentialSourceTopologyStarter implements TopologyStarter {
//
//    private static final long serialVersionUID = -2323666993205665265L;
//
//    private final PrequentialSourceProcessor psp;
//    private Stream inputStream;
//    private final int numInstanceSent;
//
//    public PrequentialSourceTopologyStarter(PrequentialSourceProcessor psp, int numInstanceSent) {
//        this.psp = psp;
//        this.numInstanceSent = numInstanceSent;
//    }
//
//    @Override
//    public void start() {
//        this.psp.sendInstances(inputStream, numInstanceSent);
//    }
//
//    public void setInputStream(Stream inputStream) {
//        this.inputStream = inputStream;
//    }
//
//}
//FIXME delete this class