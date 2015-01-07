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

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Utils;

/**
 * Classification evaluator that updates evaluation results using a sliding
 * window.
 *
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class WindowClassificationPerformanceEvaluator extends AbstractMOAObject implements
        ClassificationPerformanceEvaluator {

     private static final long serialVersionUID = 1L;

    public IntOption widthOption = new IntOption("width",
            'w', "Size of Window", 1000);

    protected double TotalweightObserved = 0;

    protected Estimator weightObserved;

    protected Estimator weightCorrect;

    protected Estimator weightCorrectNoChangeClassifier;

    protected double lastSeenClass;

    protected Estimator[] columnKappa;

    protected Estimator[] rowKappa;

    protected Estimator[] classAccuracy;

    protected int numClasses;

    public class Estimator {

        protected double[] window;

        protected int posWindow;

        protected int lenWindow;

        protected int SizeWindow;

        protected double sum;

        public Estimator(int sizeWindow) {
            window = new double[sizeWindow];
            SizeWindow = sizeWindow;
            posWindow = 0;
            lenWindow = 0;
        }

        public void add(double value) {
            sum -= window[posWindow];
            sum += value;
            window[posWindow] = value;
            posWindow++;
            if (posWindow == SizeWindow) {
                posWindow = 0;
            }
            if (lenWindow < SizeWindow) {
                lenWindow++;
            }
        }

        public double total() {
            return sum;
        }

        public double length() {
            return lenWindow;
        }

    }

    /*   public void setWindowWidth(int w) {
     this.width = w;
     reset();
     }*/
    @Override
    public void reset() {
        reset(this.numClasses);
    }

    public void reset(int numClasses) {
        this.numClasses = numClasses;
        this.rowKappa = new Estimator[numClasses];
        this.columnKappa = new Estimator[numClasses];
        this.classAccuracy = new Estimator[numClasses];
        for (int i = 0; i < this.numClasses; i++) {
            this.rowKappa[i] = new Estimator(this.widthOption.getValue());
            this.columnKappa[i] = new Estimator(this.widthOption.getValue());
            this.classAccuracy[i] = new Estimator(this.widthOption.getValue());
        }
        this.weightCorrect = new Estimator(this.widthOption.getValue());
        this.weightCorrectNoChangeClassifier = new Estimator(this.widthOption.getValue());
        this.weightObserved = new Estimator(this.widthOption.getValue());
        this.TotalweightObserved = 0;
        this.lastSeenClass = 0;
    }

    @Override
    public void addResult(Instance inst, double[] classVotes) {
        double weight = inst.weight();
        int trueClass = (int) inst.classValue();
        if (weight > 0.0) {
            if (TotalweightObserved == 0) {
                reset(inst.numClasses()); 
            }
            this.TotalweightObserved += weight;
            this.weightObserved.add(weight);
            int predictedClass = Utils.maxIndex(classVotes);
            if (predictedClass == trueClass) {
                this.weightCorrect.add(weight);
            } else {
                this.weightCorrect.add(0);
            }
            //Add Kappa statistic information
            for (int i = 0; i < this.numClasses; i++) {
                this.rowKappa[i].add(i == predictedClass ? weight : 0);
                this.columnKappa[i].add(i == trueClass ? weight : 0);
            }
            if (this.lastSeenClass == trueClass) {
                this.weightCorrectNoChangeClassifier.add(weight);
            } else {
                this.weightCorrectNoChangeClassifier.add(0);
            }
            this.classAccuracy[trueClass].add(predictedClass == trueClass ? weight : 0.0);
            this.lastSeenClass = trueClass;
        }
    }

    @Override
    public Measurement[] getPerformanceMeasurements() {
        return new Measurement[]{
            new Measurement("classified instances",
            this.TotalweightObserved),
            new Measurement("classifications correct (percent)",
            getFractionCorrectlyClassified() * 100.0),
            new Measurement("Kappa Statistic (percent)",
            getKappaStatistic() * 100.0),
            new Measurement("Kappa Temporal Statistic (percent)",
            getKappaTemporalStatistic() * 100.0)
        };

    }

    public double getTotalWeightObserved() {
        return this.weightObserved.total();
    }

    public double getFractionCorrectlyClassified() {
        return this.weightObserved.total() > 0.0 ? this.weightCorrect.total()
                / this.weightObserved.total() : 0.0;
    }

    public double getKappaStatistic() {
        if (this.weightObserved.total() > 0.0) {
            double p0 = this.weightCorrect.total() / this.weightObserved.total();
            double pc = 0;
            for (int i = 0; i < this.numClasses; i++) {
                pc += (this.rowKappa[i].total() / this.weightObserved.total())
                        * (this.columnKappa[i].total() / this.weightObserved.total());
            }
            return (p0 - pc) / (1 - pc);
        } else {
            return 0;
        }
    }

    public double getKappaTemporalStatistic() {
        if (this.weightObserved.total() > 0.0) {
            double p0 = this.weightCorrect.total() / this.weightObserved.total();
            double pc = this.weightCorrectNoChangeClassifier.total() / this.weightObserved.total();

            return (p0 - pc) / (1 - pc);
        } else {
            return 0;
        }
    }

    public double getFractionIncorrectlyClassified() {
        return 1.0 - getFractionCorrectlyClassified();
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        Measurement.getMeasurementsDescription(getPerformanceMeasurements(),
                sb, indent);
    }

}
