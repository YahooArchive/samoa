package com.yahoo.labs.samoa.streams;

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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;

/**
 * Prequential Source Processor is the processor for Prequential Evaluation Task.
 * 
 * @author Arinto Murdopo
 * 
 */
public final class PrequentialSourceProcessor implements EntranceProcessor {

    private static final long serialVersionUID = 4169053337917578558L;

    private static final Logger logger = LoggerFactory.getLogger(PrequentialSourceProcessor.class);
    private boolean isInited = false;
    private StreamSource streamSource;
    private Instance firstInstance;
    private int numberInstances;
    private int numInstanceSent = 0;

    protected InstanceStream sourceStream;
    
    /*
	 * ScheduledExecutorService to schedule sending events after each delay interval.
	 * It is expected to have only one event in the queue at a time, so we need only 
	 * one thread in the pool.
	 */
	private transient ScheduledExecutorService timer;
	private transient ScheduledFuture<?> schedule = null;
	private int readyEventIndex = 1; // No waiting for the first event
	private int delay = 0;
	private int batchSize = 1;
    private boolean finished = false;

    @Override
    public boolean process(ContentEvent event) {
        // TODO: possible refactor of the super-interface implementation
        // of source processor does not need this method
        return false;
    }
    
    @Override
    public boolean isFinished() {
    	return finished;
    }

    @Override
    public boolean hasNext() {
        return !isFinished() && (delay <= 0 || numInstanceSent < readyEventIndex);
    }

    private boolean hasReachedEndOfStream() {
        return (!streamSource.hasMoreInstances() || (numberInstances >= 0 && numInstanceSent >= numberInstances));
    }

    @Override
    public ContentEvent nextEvent() {
        InstanceContentEvent contentEvent = null;
        if (hasReachedEndOfStream()) {
        	contentEvent = new InstanceContentEvent(-1, firstInstance, false, true);
            contentEvent.setLast(true);
            // set finished status _after_ tagging last event
            finished = true;
        }
        else if (hasNext()) {
            numInstanceSent++;
            contentEvent = new InstanceContentEvent(numInstanceSent, nextInstance(), true, true);
            
            // first call to this method will trigger the timer
            if (schedule == null && delay > 0) {
                schedule = timer.scheduleWithFixedDelay(new DelayTimeoutHandler(this), delay, delay,
                        TimeUnit.MICROSECONDS);
            }
        }
        return contentEvent;
    }
    
	private void increaseReadyEventIndex() {
		readyEventIndex+=batchSize;
		// if we exceed the max, cancel the timer
		if (schedule != null && isFinished()) {
			schedule.cancel(false);
		}
	}

    @Override
    public void onCreate(int id) {
        initStreamSource(sourceStream);
        timer = Executors.newScheduledThreadPool(1);
        logger.debug("Creating PrequentialSourceProcessor with id {}", id);
    }

    @Override
    public Processor newProcessor(Processor p) {
        PrequentialSourceProcessor newProcessor = new PrequentialSourceProcessor();
        PrequentialSourceProcessor originProcessor = (PrequentialSourceProcessor) p;
        if (originProcessor.getStreamSource() != null) {
            newProcessor.setStreamSource(originProcessor.getStreamSource().getStream());
        }
        return newProcessor;
    }

//    /**
//     * Method to send instances via input stream
//     * 
//     * @param inputStream
//     * @param numberInstances
//     */
//    public void sendInstances(Stream inputStream, int numberInstances) {
//        int numInstanceSent = 0;
//        initStreamSource(sourceStream);
//
//        while (streamSource.hasMoreInstances() && numInstanceSent < numberInstances) {
//            numInstanceSent++;
//            InstanceContentEvent contentEvent = new InstanceContentEvent(numInstanceSent, nextInstance(), true, true);
//            inputStream.put(contentEvent);
//        }
//
//        sendEndEvaluationInstance(inputStream);
//    }

    public StreamSource getStreamSource() {
        return streamSource;
    }

    public void setStreamSource(InstanceStream stream) {
        this.sourceStream = stream;
    }

    public Instances getDataset() {
        if (firstInstance == null) {
            initStreamSource(sourceStream);
        }
        return firstInstance.dataset();
    }

    private Instance nextInstance() {
        if (this.isInited) {
            return streamSource.nextInstance().getData();
        } else {
            this.isInited = true;
            return firstInstance;
        }
    }

//    private void sendEndEvaluationInstance(Stream inputStream) {
//        InstanceContentEvent contentEvent = new InstanceContentEvent(-1, firstInstance, false, true);
//        contentEvent.setLast(true);
//        inputStream.put(contentEvent);
//    }

    private void initStreamSource(InstanceStream stream) {
        if (stream instanceof AbstractOptionHandler) {
            ((AbstractOptionHandler) (stream)).prepareForUse();
        }

        this.streamSource = new StreamSource(stream);
        firstInstance = streamSource.nextInstance().getData();
    }

    public void setMaxNumInstances(int value) {
        numberInstances = value;
    }
    
    public int getMaxNumInstances() {
    	return this.numberInstances;
    }
    
	public void setSourceDelay(int delay) {
		this.delay = delay;
	}

	public int getSourceDelay() {
		return this.delay;
	}
	
	public void setDelayBatchSize(int batch) {
		this.batchSize = batch;
	}
	
	private class DelayTimeoutHandler implements Runnable {
    	
    	private PrequentialSourceProcessor processor;
    	
    	public DelayTimeoutHandler(PrequentialSourceProcessor processor) {
    		this.processor = processor;
    	}
    	
    	public void run() {
    		processor.increaseReadyEventIndex();
    	}
    }
}
