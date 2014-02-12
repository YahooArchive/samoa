package com.yahoo.labs.samoa;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Option;
import com.yahoo.labs.samoa.tasks.Task;
import com.yahoo.labs.samoa.topology.impl.ThreadsComponentFactory;
import com.yahoo.labs.samoa.topology.impl.ThreadsEngine;

public class LocalThreadsDoTask {
	// TODO: clean up this class for helping ML Developer in SAMOA
    // TODO: clean up code from storm-impl
    private static final String SUPPRESS_STATUS_OUT_MSG = "Suppress the task status output. Normally it is sent to stderr.";
    private static final String SUPPRESS_RESULT_OUT_MSG = "Suppress the task result output. Normally it is sent to stdout.";
    private static final String STATUS_UPDATE_FREQ_MSG = "Wait time in milliseconds between status updates.";
    private static final Logger logger = LoggerFactory.getLogger(LocalThreadsDoTask.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {

        ArrayList<String> tmpArgs = new ArrayList<String>(Arrays.asList(args));
        
        // Get parameters for multithreading mode (number of threads and delay)
        int numThreads = 0;
        int delay = 0;
        try {
        	int pos = tmpArgs.size() - 1;
        	delay = Integer.parseInt(tmpArgs.get(pos));
        	tmpArgs.remove(pos);
        	pos--;
        	numThreads = Integer.parseInt(tmpArgs.get(pos));
        	tmpArgs.remove(pos);
        } catch (Exception e) {
        	System.out.println("Please make sure that you set numthreads and sourcedelay properties in bin/samoa-threads.properties");
        	System.out.println(e.getStackTrace());
        }

        args = tmpArgs.toArray(new String[0]);

        FlagOption suppressStatusOutOpt = new FlagOption("suppressStatusOut", 'S', SUPPRESS_STATUS_OUT_MSG);

        FlagOption suppressResultOutOpt = new FlagOption("suppressResultOut", 'R', SUPPRESS_RESULT_OUT_MSG);

        IntOption statusUpdateFreqOpt = new IntOption("statusUpdateFrequency", 'F', STATUS_UPDATE_FREQ_MSG, 1000, 0, Integer.MAX_VALUE);

        Option[] extraOptions = new Option[] { suppressStatusOutOpt, suppressResultOutOpt, statusUpdateFreqOpt };

        StringBuilder cliString = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            cliString.append(" ").append(args[i]);
        }
        logger.debug("Command line string = {}", cliString.toString());
        System.out.println("Command line string = " + cliString.toString());

        Task task = null;
        try {
            task = (Task) ClassOption.cliStringToObject(cliString.toString(), Task.class, extraOptions);
            logger.info("Sucessfully instantiating {}", task.getClass().getCanonicalName());
        } catch (Exception e) {
            logger.error("Fail to initialize the task", e);
            System.out.println("Fail to initialize the task" + e);
            return;
        }
        task.setFactory(new ThreadsComponentFactory());
        task.init();
        ThreadsEngine.submitTopology(task.getTopology(), delay, numThreads);
    }
}
