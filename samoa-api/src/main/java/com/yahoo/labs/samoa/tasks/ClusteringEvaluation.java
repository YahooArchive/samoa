package com.yahoo.labs.samoa.tasks;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.github.javacliparser.FileOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;
import com.yahoo.labs.samoa.evaluation.ClusteringEvaluatorProcessor;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.learners.clusterers.simple.ClusteringDistributorProcessor;
import com.yahoo.labs.samoa.learners.clusterers.simple.DistributedClusterer;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;
import com.yahoo.labs.samoa.moa.streams.clustering.ClusteringStream;
import com.yahoo.labs.samoa.moa.streams.clustering.RandomRBFGeneratorEvents;
import com.yahoo.labs.samoa.streams.ClusteringEntranceProcessor;
import com.yahoo.labs.samoa.topology.ComponentFactory;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

/**
 * A task that runs and evaluates a distributed clustering algorithm.
 * 
 */
public class ClusteringEvaluation implements Task, Configurable {

    private static final long serialVersionUID = -8246537378371580550L;

    private static final int DISTRIBUTOR_PARALLELISM = 1;

    private static final Logger logger = LoggerFactory.getLogger(ClusteringEvaluation.class);

    public ClassOption learnerOption = new ClassOption("learner", 'l', "Clustering to run.", Learner.class, DistributedClusterer.class.getName());

    public ClassOption streamTrainOption = new ClassOption("streamTrain", 's', "Input stream.", InstanceStream.class,
            RandomRBFGeneratorEvents.class.getName());

    public IntOption instanceLimitOption = new IntOption("instanceLimit", 'i', "Maximum number of instances to test/train on  (-1 = no limit).", 100000, -1,
            Integer.MAX_VALUE);

    public IntOption measureCollectionTypeOption = new IntOption("measureCollectionType", 'm', "Type of measure collection", 0, 0, Integer.MAX_VALUE);

    public IntOption timeLimitOption = new IntOption("timeLimit", 't', "Maximum number of seconds to test/train for (-1 = no limit).", -1, -1,
            Integer.MAX_VALUE);

    public IntOption sampleFrequencyOption = new IntOption("sampleFrequency", 'f', "How many instances between samples of the learning performance.", 1000, 0,
            Integer.MAX_VALUE);

    public StringOption evaluationNameOption = new StringOption("evaluationName", 'n', "Identifier of the evaluation", "Clustering__"
            + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

    public FileOption dumpFileOption = new FileOption("dumpFile", 'd', "File to append intermediate csv results to", null, "csv", true);

    public FloatOption samplingThresholdOption = new FloatOption("samplingThreshold", 'a', "Ratio of instances sampled that will be used for evaluation.", 0.5,
            0.0, 1.0);

    private ClusteringEntranceProcessor source;
    private InstanceStream streamTrain;
    private ClusteringDistributorProcessor distributor;
    private Stream distributorStream;
    private Stream evaluationStream;
    
    // Default=0: no delay/waiting
    public IntOption sourceDelayOption = new IntOption("sourceDelay", 'w', "How many miliseconds between injections of two instances.", 0, 0, Integer.MAX_VALUE);
    
    private Learner learner;
    private ClusteringEvaluatorProcessor evaluator;

    private Topology topology;
    private TopologyBuilder builder;

    public void getDescription(StringBuilder sb) {
        sb.append("Clustering evaluation");
    }

    @Override
    public void init() {
        // TODO remove the if statement theoretically, dynamic binding will work here! for now, the if statement is used by Storm

        if (builder == null) {
            logger.warn("Builder was not initialized, initializing it from the Task");

            builder = new TopologyBuilder();
            logger.debug("Successfully instantiating TopologyBuilder");

            builder.initTopology(evaluationNameOption.getValue(), sourceDelayOption.getValue());
            logger.debug("Successfully initializing SAMOA topology with name {}", evaluationNameOption.getValue());
        }

        // instantiate ClusteringEntranceProcessor and its output stream (sourceStream)
        source = new ClusteringEntranceProcessor();
        streamTrain = this.streamTrainOption.getValue();
        source.setStreamSource(streamTrain);
        builder.addEntranceProcessor(source);
        source.setSamplingThreshold(samplingThresholdOption.getValue());
        source.setMaxNumInstances(instanceLimitOption.getValue());
        logger.debug("Successfully instantiated ClusteringEntranceProcessor");

        Stream sourceStream = builder.createStream(source);
        // starter.setInputStream(sourcePiOutputStream); // FIXME set stream in the EntrancePI

        // distribution of instances and sampling for evaluation
        distributor = new ClusteringDistributorProcessor();
        builder.addProcessor(distributor, DISTRIBUTOR_PARALLELISM);
        builder.connectInputShuffleStream(sourceStream, distributor);
        distributorStream = builder.createStream(distributor);
        distributor.setOutputStream(distributorStream);
        evaluationStream = builder.createStream(distributor);
        distributor.setEvaluationStream(evaluationStream); // passes evaluation events along
        logger.debug("Successfully instantiated Distributor");
       
        // instantiate learner and connect it to distributorStream
        learner = this.learnerOption.getValue();
        learner.init(builder, source.getDataset(), 1);
        builder.connectInputShuffleStream(distributorStream, learner.getInputProcessor());
        logger.debug("Successfully instantiated Learner");

        evaluator = new ClusteringEvaluatorProcessor.Builder(
        sampleFrequencyOption.getValue()).dumpFile(dumpFileOption.getFile())
            .decayHorizon(((ClusteringStream) streamTrain).getDecayHorizon()).build();

        builder.addProcessor(evaluator);
        for (Stream evaluatorPiInputStream:learner.getResultStreams()) {
        	builder.connectInputShuffleStream(evaluatorPiInputStream, evaluator);
        }
        builder.connectInputAllStream(evaluationStream, evaluator);
        logger.debug("Successfully instantiated EvaluatorProcessor");

        topology = builder.build();
        logger.debug("Successfully built the topology");
    }

    @Override
    public void setFactory(ComponentFactory factory) {
        // TODO unify this code with init() for now, it's used by S4 App
        // dynamic binding theoretically will solve this problem
        builder = new TopologyBuilder(factory);
        logger.debug("Successfully instantiated TopologyBuilder");

        builder.initTopology(evaluationNameOption.getValue());
        logger.debug("Successfully initialized SAMOA topology with name {}", evaluationNameOption.getValue());

    }

    public Topology getTopology() {
        return topology;
    }
}
