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

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.moa.core.Measurement;

/**
 * Regression evaluator that performs basic incremental evaluation.
 *
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class BasicRegressionPerformanceEvaluator extends AbstractMOAObject
        implements RegressionPerformanceEvaluator {

    private static final long serialVersionUID = 1L;

    protected double weightObserved;

    protected double squareError;

    protected double averageError;
   
    protected double sumTarget;
   
    protected double squareTargetError;
   
    protected double averageTargetError;

    @Override
    public void reset() {
        this.weightObserved = 0.0;
        this.squareError = 0.0;
        this.averageError = 0.0;
        this.sumTarget = 0.0;
        this.averageTargetError = 0.0;
        this.squareTargetError = 0.0;
       
    }

    @Override
    public void addResult(Instance inst, double[] prediction) {
    	double weight = inst.weight();
        double classValue = inst.classValue();
        if (weight > 0.0) {
            if (prediction.length > 0) {
                double meanTarget = this.weightObserved != 0 ?
                            this.sumTarget / this.weightObserved : 0.0;
                this.squareError += (classValue - prediction[0]) * (classValue - prediction[0]);
                this.averageError += Math.abs(classValue - prediction[0]);
                this.squareTargetError += (classValue - meanTarget) * (classValue - meanTarget);
                this.averageTargetError += Math.abs(classValue - meanTarget);
                this.sumTarget += classValue;
                this.weightObserved += weight;
            }
        }
    }

    @Override
    public Measurement[] getPerformanceMeasurements() {
        return new Measurement[]{
                    new Measurement("classified instances",
                    getTotalWeightObserved()),
                    new Measurement("mean absolute error",
                    getMeanError()),
                    new Measurement("root mean squared error",
                    getSquareError()),
                    new Measurement("relative mean absolute error",
                    getRelativeMeanError()),
                    new Measurement("relative root mean squared error",
                    getRelativeSquareError())
        };
    }

    public double getTotalWeightObserved() {
        return this.weightObserved;
    }

    public double getMeanError() {
        return this.weightObserved > 0.0 ? this.averageError
                / this.weightObserved : 0.0;
    }

    public double getSquareError() {
        return Math.sqrt(this.weightObserved > 0.0 ? this.squareError
                / this.weightObserved : 0.0);
    }
   
    public double getTargetMeanError() {
        return this.weightObserved > 0.0 ? this.averageTargetError
                / this.weightObserved : 0.0;
    }

    public double getTargetSquareError() {
        return Math.sqrt(this.weightObserved > 0.0 ? this.squareTargetError
                / this.weightObserved : 0.0);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        Measurement.getMeasurementsDescription(getPerformanceMeasurements(),
                sb, indent);
    }

    private double getRelativeMeanError() {
        return this.averageTargetError> 0 ?
                this.averageError/this.averageTargetError : 0.0;
    }

    private double getRelativeSquareError() {
    	return Math.sqrt(this.squareTargetError> 0 ?
                this.squareError/this.squareTargetError : 0.0);
    }
}
