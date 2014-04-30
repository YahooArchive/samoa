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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.evaluation.ClusteringEvaluationContentEvent;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.clusterers.ClusteringContentEvent;
import com.yahoo.labs.samoa.moa.cluster.Clustering;
import com.yahoo.labs.samoa.moa.core.DataPoint;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;
import com.yahoo.labs.samoa.moa.streams.clustering.ClusteringStream;
import com.yahoo.labs.samoa.moa.streams.clustering.RandomRBFGeneratorEvents;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * Clustering Source Processor is the processor for Clustering Evaluation Task.
 * 
 * @author Arinto Murdopo
 * 
 */
public final class ClusteringSourceProcessor implements EntranceProcessor {

    private static final long serialVersionUID = 4169053337917578558L;

    private static final Logger logger = LoggerFactory.getLogger(ClusteringSourceProcessor.class);

    private StreamSource streamSource;
    private Instance firstInstance;
    private boolean isInited = false;
    private Random random = new Random();
    private int id;
    private double samplingThreshold;

    @Override
    public boolean process(ContentEvent event) {
        // TODO: possible refactor of the super-interface implementation
        // of source processor does not need this method
        return false;
    }

    @Override
    public void onCreate(int id) {
        this.id = id;
        logger.debug("Creating ClusteringSourceProcessor with id {}", this.id);
    }

    @Override
    public Processor newProcessor(Processor p) {
        ClusteringSourceProcessor newProcessor = new ClusteringSourceProcessor();
        ClusteringSourceProcessor originProcessor = (ClusteringSourceProcessor) p;
        if (originProcessor.getStreamSource() != null) {
            newProcessor.setStreamSource(originProcessor.getStreamSource().getStream());
        }
        return newProcessor;
    }

    @Override
    public boolean hasNext() {
        // return streamSource.hasMoreInstances() && numInstanceSent < numberInstances;
        return true; // FIXME
    }
    
    @Override
    public boolean isFinished() {
    	return false; // FIXME
    }

    /**
     * Method to send instances via input stream
     * 
     * @param inputStream
     * @param numberInstances
     */
    public void sendInstances(Stream inputStream, Stream evaluationStream, int numberInstances, double samplingThreshold) {
        int numInstanceSent = 0;
        this.samplingThreshold = samplingThreshold;
        while (streamSource.hasMoreInstances() && numInstanceSent < numberInstances) {
            numInstanceSent++;
            DataPoint nextDataPoint = new DataPoint(nextInstance(), numInstanceSent);
            ClusteringContentEvent contentEvent = new ClusteringContentEvent(numInstanceSent, nextDataPoint);
            inputStream.put(contentEvent);
            sendPointsAndGroundTruth(streamSource, evaluationStream, numInstanceSent, nextDataPoint);
        }

        sendEndEvaluationInstance(inputStream);
    }

    public StreamSource getStreamSource() {
        return streamSource;
    }

    public void setStreamSource(InstanceStream stream) {
        if (stream instanceof AbstractOptionHandler) {
            ((AbstractOptionHandler) (stream)).prepareForUse();
        }

        this.streamSource = new StreamSource(stream);
        firstInstance = streamSource.nextInstance().getData();
    }

    public Instances getDataset() {
        return firstInstance.dataset();
    }

    private Instance nextInstance() {
        if (this.isInited == true) {
            return streamSource.nextInstance().getData();
        } else {
            this.isInited = true;
            return firstInstance;
        }
    }

    private void sendEndEvaluationInstance(Stream inputStream) {
        ClusteringContentEvent contentEvent = new ClusteringContentEvent(-1, firstInstance);
        contentEvent.setLast(true);
        inputStream.put(contentEvent);
    }

    private void sendPointsAndGroundTruth(StreamSource sourceStream, Stream evaluationStream, int numInstanceSent, DataPoint nextDataPoint) {
        boolean sendEvent = false;
        DataPoint instance = null;
        Clustering gtClustering = null;
        int samplingFrequency = ((ClusteringStream) sourceStream.getStream()).getDecayHorizon();
        if (random.nextDouble() < samplingThreshold) {
            // Add instance
            sendEvent = true;
            instance = nextDataPoint;
        }
        if (numInstanceSent > 0 && numInstanceSent % samplingFrequency == 0) {
            // Add GroundTruth
            sendEvent = true;
            gtClustering = ((RandomRBFGeneratorEvents) sourceStream.getStream()).getGeneratingClusters();
        }
        if (sendEvent == true) {
            ClusteringEvaluationContentEvent evalEvent;
            evalEvent = new ClusteringEvaluationContentEvent(gtClustering, instance, false);
            evaluationStream.put(evalEvent);
        }
    }

    @Override
    public ContentEvent nextEvent() {
        // TODO Auto-generated method stub
        return null;
    }
}
