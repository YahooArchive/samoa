package com.yahoo.labs.samoa;

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

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.tasks.Task;
import com.yahoo.labs.samoa.topology.impl.ParallelComponentFactory;
import com.yahoo.labs.samoa.topology.impl.ParallelEngine;
import com.yahoo.labs.samoa.topology.impl.SimpleComponentFactory;
import com.yahoo.labs.samoa.topology.impl.SimpleEngine;
import com.github.javacliparser.ClassOption;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Option;
import com.yahoo.labs.samoa.topology.impl.SimpleComponentFactory;
import com.yahoo.labs.samoa.topology.impl.SimpleEngine;

/**
 * The Class DoTask.
 */
public class LocalDoTask {

    // TODO: clean up this class for helping ML Developer in SAMOA
    // ======= TODO: clean up code from storm-impl
    private static final String SUPPRESS_STATUS_OUT_MSG = "Suppress the task status output. Normally it is sent to stderr.";
    private static final String SUPPRESS_RESULT_OUT_MSG = "Suppress the task result output. Normally it is sent to stdout.";
    private static final String STATUS_UPDATE_FREQ_MSG = "Wait time in milliseconds between status updates.";
    private static final Logger logger = LoggerFactory.getLogger(LocalDoTask.class);

    /**
     * The main method.
     *
     * @param args : the arguments
     */
    public static void main(String[] args) {

        ArrayList<String> tmpArgs = new ArrayList<String>(Arrays.asList(args));
        
        // Check if number of threads is specified
        int pos = tmpArgs.size() - 1;
        int numThreads = 0;
        try {
        	numThreads = Integer.parseInt(tmpArgs.get(pos));
        	tmpArgs.remove(pos);
        }
        catch (NumberFormatException e) {
        	// do nothing
        }

        args = tmpArgs.toArray(new String[0]);

        FlagOption suppressStatusOutOpt = new FlagOption("suppressStatusOut",
                'S', SUPPRESS_STATUS_OUT_MSG);

        FlagOption suppressResultOutOpt = new FlagOption("suppressResultOut",
                'R', SUPPRESS_RESULT_OUT_MSG);

        IntOption statusUpdateFreqOpt = new IntOption("statusUpdateFrequency",
                'F', STATUS_UPDATE_FREQ_MSG, 1000, 0, Integer.MAX_VALUE);

        Option[] extraOptions = new Option[]{suppressStatusOutOpt,
            suppressResultOutOpt, statusUpdateFreqOpt};

        StringBuilder cliString = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            cliString.append(" ").append(args[i]);
        }
        logger.debug("Command line string = {}", cliString.toString());
        System.out.println("Command line string = " + cliString.toString());

        Task task = null;
        try {
            task = (Task) ClassOption.cliStringToObject(cliString.toString(),
                    Task.class, extraOptions);
            logger.info("Sucessfully instantiating {}", task.getClass()
                    .getCanonicalName());
        } catch (Exception e) {
            logger.error("Fail to initialize the task", e);
            System.out.println("Fail to initialize the task" + e);
            return;
        }
        
        // depend on the user-specified numThreads
        // we either call Simple-package or Parallel-package
        // This is because I need to compare the 2 packages
        if (numThreads > 1) {
        	logger.info("Will be running with multithreading");
        	task.setFactory(new ParallelComponentFactory());
            task.init();
            ParallelEngine.setNumberOfThreads(numThreads);
            ParallelEngine.submitTopology(task.getTopology());
        }
        else {
        	logger.info("Will be running with the Simple-package");
        	task.setFactory(new SimpleComponentFactory());
            task.init();
            SimpleEngine.submitTopology(task.getTopology());
        }
        
    }
}
