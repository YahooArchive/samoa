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

import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Utils;

/**
 * Classification evaluator that performs basic incremental evaluation.
 * 
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class BasicClassificationPerformanceEvaluator extends AbstractMOAObject implements
        ClassificationPerformanceEvaluator {

    private static final long serialVersionUID = 1L;

    protected double weightObserved;

    protected double weightCorrect;

    protected double[] columnKappa;

    protected double[] rowKappa;

    protected int numClasses;

    private double weightCorrectNoChangeClassifier;

    private int lastSeenClass;

    @Override
    public void reset() {
        reset(this.numClasses);
    }

    public void reset(int numClasses) {
        this.numClasses = numClasses;
        this.rowKappa = new double[numClasses];
        this.columnKappa = new double[numClasses];
        for (int i = 0; i < this.numClasses; i++) {
            this.rowKappa[i] = 0.0;
            this.columnKappa[i] = 0.0;
        }
        this.weightObserved = 0.0;
        this.weightCorrect = 0.0;
        this.weightCorrectNoChangeClassifier = 0.0;
        this.lastSeenClass = 0;
    }

    @Override
    public void addResult(Instance inst, double[] classVotes) {
        double weight = inst.weight();
        int trueClass = (int) inst.classValue();
        if (weight > 0.0) {
            if (this.weightObserved == 0) {
                reset(inst.numClasses()); 
            }
            this.weightObserved += weight;
            int predictedClass = Utils.maxIndex(classVotes);
            if (predictedClass == trueClass) {
                this.weightCorrect += weight;
            }
            if(rowKappa.length > 0){
            	this.rowKappa[predictedClass] += weight;
            }       
            if (columnKappa.length > 0) {
                this.columnKappa[trueClass] += weight;
            }
        }
        if (this.lastSeenClass == trueClass) {
            this.weightCorrectNoChangeClassifier += weight;
        }
        this.lastSeenClass = trueClass;
    }

    @Override
    public Measurement[] getPerformanceMeasurements() {
        return new Measurement[]{
            new Measurement("classified instances",
            getTotalWeightObserved()),
            new Measurement("classifications correct (percent)",
            getFractionCorrectlyClassified() * 100.0),
            new Measurement("Kappa Statistic (percent)",
            getKappaStatistic() * 100.0),
            new Measurement("Kappa Temporal Statistic (percent)",
            getKappaTemporalStatistic() * 100.0)
        };

    }

    public double getTotalWeightObserved() {
        return this.weightObserved;
    }

    public double getFractionCorrectlyClassified() {
        return this.weightObserved > 0.0 ? this.weightCorrect
                / this.weightObserved : 0.0;
    }

    public double getFractionIncorrectlyClassified() {
        return 1.0 - getFractionCorrectlyClassified();
    }

    public double getKappaStatistic() {
        if (this.weightObserved > 0.0) {
            double p0 = getFractionCorrectlyClassified();
            double pc = 0.0;
            for (int i = 0; i < this.numClasses; i++) {
                pc += (this.rowKappa[i] / this.weightObserved)
                        * (this.columnKappa[i] / this.weightObserved);
            }
            return (p0 - pc) / (1.0 - pc);
        } else {
            return 0;
        }
    }

    public double getKappaTemporalStatistic() {
        if (this.weightObserved > 0.0) {
            double p0 = this.weightCorrect / this.weightObserved;
            double pc = this.weightCorrectNoChangeClassifier / this.weightObserved;

            return (p0 - pc) / (1.0 - pc);
        } else {
            return 0;
        }
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        Measurement.getMeasurementsDescription(getPerformanceMeasurements(),
                sb, indent);
    }
}
