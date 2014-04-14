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

/**
 * License
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.s4.base.Event;
import org.apache.s4.base.KeyFinder;
import org.apache.s4.core.App;
import org.apache.s4.core.ProcessingElement;
import org.apache.s4.core.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.javacliparser.Option;
import com.github.javacliparser.ClassOption;
import com.yahoo.labs.samoa.core.Globals;
import com.yahoo.labs.samoa.tasks.Task;
import com.yahoo.labs.samoa.topology.ComponentFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/*
 * S4 App that runs samoa Tasks
 *
 * */

/**
 * The Class DoTaskApp.
 */
final public class S4DoTask extends App {

    private final Logger logger = LoggerFactory.getLogger(S4DoTask.class);
    Task task;

    @Inject @Named("evalTask") public String evalTask;

    public S4DoTask() {
        super();
    }

    /** The engine. */
    protected ComponentFactory componentFactory;

    /**
     * Gets the factory.
     * 
     * @return the factory
     */
    public ComponentFactory getFactory() {
        return componentFactory;
    }

    /**
     * Sets the factory.
     * 
     * @param factory
     *            the new factory
     */
    public void setFactory(ComponentFactory factory) {
        this.componentFactory = factory;
    }

    /*
     * Build the application
     * 
     * @see org.apache.s4.core.App#onInit()
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#onInit()
     */
    @Override
    protected void onInit() {
        logger.info("DoTaskApp onInit");
        // ConsoleReporters prints S4 metrics
        // MetricsRegistry mr = new MetricsRegistry();
        //
        // CsvReporter.enable(new File(System.getProperty("user.home")
        // + "/monitor/"), 10, TimeUnit.SECONDS);
        // ConsoleReporter.enable(10, TimeUnit.SECONDS);
        try {
            System.err.println();
            System.err.println(Globals.getWorkbenchInfoString());
            System.err.println();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        S4ComponentFactory factory = new S4ComponentFactory();
        factory.setApp(this);

        // logger.debug("LC {}", lc);

        // task = TaskProvider.getTask(evalTask);

        // EXAMPLE OPTIONS
        // -l Clustream -g Clustream -i 100000 -s (RandomRBFGeneratorEvents -K
        // 5 -N 0.0)
        // String[] args = new String[] {evalTask,"-l", "Clustream","-g",
        // "Clustream", "-i", "100000", "-s", "(RamdomRBFGeneratorsEvents",
        // "-K", "5", "-N", "0.0)"};
        // String[] args = new String[] { evalTask, "-l", "clustream.Clustream",
        // "-g", "clustream.Clustream", "-i", "100000", "-s",
        // "(RandomRBFGeneratorEvents", "-K", "5", "-N", "0.0)" };
        logger.debug("PARAMETERS {}", evalTask);
        // params = params.replace(":", " ");
        List<String> parameters = new ArrayList<String>();
        // parameters.add(evalTask);
        try {
            parameters.addAll(Arrays.asList(URLDecoder.decode(evalTask, "UTF-8").split(" ")));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        String[] args = parameters.toArray(new String[0]);
        Option[] extraOptions = new Option[] {};
        // build a single string by concatenating cli options
        StringBuilder cliString = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            cliString.append(" ").append(args[i]);
        }

        // parse options
        try {
            task = (Task) ClassOption.cliStringToObject(cliString.toString(), Task.class, extraOptions);
            task.setFactory(factory);
            task.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#onStart()
     */
    @Override
    protected void onStart() {
        logger.info("Starting DoTaskApp... App Partition [{}]", this.getPartitionId());
        // <<<<<<< HEAD Task doesn't have start in latest storm-impl
        // TODO change the way the app starts
        // if (this.getPartitionId() == 0)
        S4Topology s4topology = (S4Topology) getTask().getTopology();
        S4EntranceProcessingItem epi = (S4EntranceProcessingItem) s4topology.getEntranceProcessingItem();
        while (epi.injectNextEvent())
            // inject events from the EntrancePI
            ;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#onClose()
     */
    @Override
    protected void onClose() {
        System.out.println("Closing DoTaskApp...");

    }

    /**
     * Gets the task.
     * 
     * @return the task
     */
    public Task getTask() {
        return task;
    }

    // These methods are protected in App and can not be accessed from outside.
    // They are
    // called from parallel classifiers and evaluations. Is there a better way
    // to do that?

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#createPE(java.lang.Class)
     */
    @Override
    public <T extends ProcessingElement> T createPE(Class<T> type) {
        return super.createPE(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#createStream(java.lang.String, org.apache.s4.base.KeyFinder, org.apache.s4.core.ProcessingElement[])
     */
    @Override
    public <T extends Event> Stream<T> createStream(String name, KeyFinder<T> finder, ProcessingElement... processingElements) {
        return super.createStream(name, finder, processingElements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.s4.core.App#createStream(java.lang.String, org.apache.s4.core.ProcessingElement[])
     */
    @Override
    public <T extends Event> Stream<T> createStream(String name, ProcessingElement... processingElements) {
        return super.createStream(name, processingElements);
    }

    // @com.beust.jcommander.Parameters(separators = "=")
    // class Parameters {
    //
    // @Parameter(names={"-lc","-local"}, description="Local clustering method")
    // private String localClustering;
    //
    // @Parameter(names={"-gc","-global"},
    // description="Global clustering method")
    // private String globalClustering;
    //
    // }
    //
    // class ParametersConverter {// implements IStringConverter<String[]> {
    //
    //
    // public String[] convertToArgs(String value) {
    //
    // String[] params = value.split(",");
    // String[] args = new String[params.length*2];
    // for(int i=0; i<params.length ; i++) {
    // args[i] = params[i].split("=")[0];
    // args[i+1] = params[i].split("=")[1];
    // i++;
    // }
    // return args;
    // }
    //
    // }

}
