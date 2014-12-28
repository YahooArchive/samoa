package com.yahoo.labs.samoa.evaluation;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.evaluation.measures.SSQ;
import com.yahoo.labs.samoa.evaluation.measures.StatisticalCollection;
import com.yahoo.labs.samoa.moa.cluster.Clustering;
import com.yahoo.labs.samoa.moa.clusterers.KMeans;
import com.yahoo.labs.samoa.moa.core.DataPoint;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.moa.evaluation.LearningCurve;
import com.yahoo.labs.samoa.moa.evaluation.LearningEvaluation;
import com.yahoo.labs.samoa.moa.evaluation.MeasureCollection;

public class ClusteringEvaluatorProcessor implements Processor {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2778051819116753612L;

    private static final Logger logger = LoggerFactory.getLogger(EvaluatorProcessor.class);

    private static final String ORDERING_MEASUREMENT_NAME = "evaluation instances";

    private final int samplingFrequency;
    private final int decayHorizon;
    private final File dumpFile;
    private transient PrintStream immediateResultStream = null;
    private transient boolean firstDump = true;

    private long totalCount = 0;
    private long experimentStart = 0;

    private LearningCurve learningCurve;

    private MeasureCollection[] measures;

    private int id;

    protected Clustering gtClustering;

    protected ArrayList<DataPoint> points;

    private ClusteringEvaluatorProcessor(Builder builder) {
        this.samplingFrequency = builder.samplingFrequency;
        this.dumpFile = builder.dumpFile;
        this.points = new ArrayList<>();
        this.decayHorizon = builder.decayHorizon;
    }

    @Override
    public boolean process(ContentEvent event) {
        boolean ret = false;
        if (event instanceof ClusteringResultContentEvent) {
            ret = process((ClusteringResultContentEvent) event);
        }
        if (event instanceof ClusteringEvaluationContentEvent) {
            ret = process((ClusteringEvaluationContentEvent) event);
        }
        return ret;
    }

    private boolean process(ClusteringResultContentEvent result) {
        // evaluate
        Clustering clustering = KMeans.gaussianMeans(gtClustering, result.getClustering());
        for (MeasureCollection measure : measures) {
            try {
                measure.evaluateClusteringPerformance(clustering, gtClustering, points);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        this.addMeasurement();

        if (result.isLastEvent()) {
            this.concludeMeasurement();
            return true;
        }

        totalCount += 1;

        if (totalCount == 1) {
            experimentStart = System.nanoTime();
        }

        return false;
    }

    private boolean process(ClusteringEvaluationContentEvent result) {
        boolean ret = false;
        if (result.getGTClustering() != null) {
            gtClustering = result.getGTClustering();
            ret = true;
        }
        if (result.getDataPoint() != null) {
            points.add(result.getDataPoint());
            if (points.size() > this.decayHorizon) {
                points.remove(0);
            }
            ret = true;
        }
        return ret;
    }

    @Override
    public void onCreate(int id) {
        this.id = id;
        this.learningCurve = new LearningCurve(ORDERING_MEASUREMENT_NAME);
        // create the measure collection
        measures = getMeasures(getMeasureSelection());

        if (this.dumpFile != null) {
            try {
                if (dumpFile.exists()) {
                    this.immediateResultStream = new PrintStream(new FileOutputStream(dumpFile, true), true);
                } else {
                    this.immediateResultStream = new PrintStream(new FileOutputStream(dumpFile), true);
                }

            } catch (FileNotFoundException e) {
                this.immediateResultStream = null;
                logger.error("File not found exception for {}:{}", this.dumpFile.getAbsolutePath(), e.toString());

            } catch (Exception e) {
                this.immediateResultStream = null;
                logger.error("Exception when creating {}:{}", this.dumpFile.getAbsolutePath(), e.toString());
            }
        }

        this.firstDump = true;
    }

    private static ArrayList<Class> getMeasureSelection() {
        ArrayList<Class> mclasses = new ArrayList<>();
        // mclasses.add(EntropyCollection.class);
        // mclasses.add(F1.class);
        // mclasses.add(General.class);
        // *mclasses.add(CMM.class);
        mclasses.add(SSQ.class);
        // *mclasses.add(SilhouetteCoefficient.class);
        mclasses.add(StatisticalCollection.class);
        // mclasses.add(Separation.class);

        return mclasses;
    }

    private static MeasureCollection[] getMeasures(ArrayList<Class> measure_classes) {
        MeasureCollection[] measures = new MeasureCollection[measure_classes.size()];
        for (int i = 0; i < measure_classes.size(); i++) {
            try {
                MeasureCollection m = (MeasureCollection) measure_classes.get(i).newInstance();
                measures[i] = m;

            } catch (Exception ex) {
                java.util.logging.Logger.getLogger("Couldn't create Instance for " + measure_classes.get(i).getName());
                ex.printStackTrace();
            }
        }
        return measures;
    }

    @Override
    public Processor newProcessor(Processor p) {
        ClusteringEvaluatorProcessor originalProcessor = (ClusteringEvaluatorProcessor) p;
        ClusteringEvaluatorProcessor newProcessor = new ClusteringEvaluatorProcessor.Builder(originalProcessor).build();

        if (originalProcessor.learningCurve != null) {
            newProcessor.learningCurve = originalProcessor.learningCurve;
        }

        return newProcessor;
    }

    @Override
    public String toString() {
        StringBuilder report = new StringBuilder();

        report.append(EvaluatorProcessor.class.getCanonicalName());
        report.append("id = ").append(this.id);
        report.append('\n');

        if (learningCurve.numEntries() > 0) {
            report.append(learningCurve.toString());
            report.append('\n');
        }
        return report.toString();
    }

    private void addMeasurement() {
        // printMeasures();
        List<Measurement> measurements = new ArrayList<>();
        measurements.add(new Measurement(ORDERING_MEASUREMENT_NAME, totalCount * this.samplingFrequency));

        addClusteringPerformanceMeasurements(measurements);
        Measurement[] finalMeasurements = measurements.toArray(new Measurement[measurements.size()]);

        LearningEvaluation learningEvaluation = new LearningEvaluation(finalMeasurements);
        learningCurve.insertEntry(learningEvaluation);
        logger.debug("evaluator id = {}", this.id);
        // logger.info(learningEvaluation.toString());

        if (immediateResultStream != null) {
            if (firstDump) {
                immediateResultStream.println(learningCurve.headerToString());
                firstDump = false;
            }

            immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
            immediateResultStream.flush();
        }
    }

    private void addClusteringPerformanceMeasurements(List<Measurement> measurements) {
        for (MeasureCollection measure : measures) {
            for (int j = 0; j < measure.getNumMeasures(); j++) {
                Measurement measurement = new Measurement(measure.getName(j), measure.getLastValue(j));
                measurements.add(measurement);
            }
        }
    }

    private void concludeMeasurement() {
        logger.info("last event is received!");
        logger.info("total count: {}", this.totalCount);

        String learningCurveSummary = this.toString();
        logger.info(learningCurveSummary);

        long experimentEnd = System.nanoTime();
        long totalExperimentTime = TimeUnit.SECONDS.convert(experimentEnd - experimentStart, TimeUnit.NANOSECONDS);
        logger.info("total evaluation time: {} seconds for {} instances", totalExperimentTime, totalCount);
        // logger.info("average throughput rate: {} instances/seconds", (totalCount/totalExperimentTime));
    }

    private void printMeasures() {
        StringBuilder sb = new StringBuilder();
        for (MeasureCollection measure : measures) {

            sb.append("Mean ").append(measure.getClass().getSimpleName()).append(":").append(measure.getNumMeasures()).append("\n");
            for (int j = 0; j < measure.getNumMeasures(); j++) {
                sb.append("[").append(measure.getName(j)).append("=").append(measure.getLastValue(j)).append("] \n");

            }
            sb.append("\n");
        }

        logger.debug("\n MEASURES: \n\n {}", sb.toString());
        System.out.println(sb.toString());
    }

    public static class Builder {

        private int samplingFrequency = 1000;
        private File dumpFile = null;
        private int decayHorizon = 1000;

        public Builder(int samplingFrequency) {
            this.samplingFrequency = samplingFrequency;
        }

        public Builder(ClusteringEvaluatorProcessor oldProcessor) {
            this.samplingFrequency = oldProcessor.samplingFrequency;
            this.dumpFile = oldProcessor.dumpFile;
            this.decayHorizon = oldProcessor.decayHorizon;
        }

        public Builder samplingFrequency(int samplingFrequency) {
            this.samplingFrequency = samplingFrequency;
            return this;
        }

        public Builder decayHorizon(int decayHorizon) {
            this.decayHorizon = decayHorizon;
            return this;
        }

        public Builder dumpFile(File file) {
            this.dumpFile = file;
            return this;
        }

        public ClusteringEvaluatorProcessor build() {
            return new ClusteringEvaluatorProcessor(this);
        }
    }
}
