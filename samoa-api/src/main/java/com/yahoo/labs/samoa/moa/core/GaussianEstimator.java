package com.yahoo.labs.samoa.moa.core;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
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

/**
 * Gaussian incremental estimator that uses incremental method that is more resistant to floating point imprecision.
 * for more info see Donald Knuth's "The Art of Computer Programming, Volume 2: Seminumerical Algorithms", section 4.2.2.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class GaussianEstimator extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected double weightSum;

    protected double mean;

    protected double varianceSum;

    public static final double NORMAL_CONSTANT = Math.sqrt(2 * Math.PI);

    public void addObservation(double value, double weight) {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            return;
        }
        if (this.weightSum > 0.0) {
            this.weightSum += weight;
            double lastMean = this.mean;
            this.mean += weight * (value - lastMean) / this.weightSum; 
            this.varianceSum += weight * (value - lastMean) * (value - this.mean);
        } else {
            this.mean = value;
            this.weightSum = weight;
        }
    }

    public void addObservations(GaussianEstimator obs) {
        // Follows Variance Combination Rule in Section 2 of
        // Brian Babcock, Mayur Datar, Rajeev Motwani, Liadan O'Callaghan:
        // Maintaining variance and k-medians over data stream windows. PODS 2003: 234-243
        //
        if ((this.weightSum >= 0.0) && (obs.weightSum > 0.0)) {
            double oldMean = this.mean;
            this.mean = (this.mean * (this.weightSum / (this.weightSum + obs.weightSum)))
                    + (obs.mean * (obs.weightSum / (this.weightSum + obs.weightSum)));
            this.varianceSum += obs.varianceSum + (this.weightSum * obs.weightSum / (this.weightSum + obs.weightSum) *
                                 Math.pow(obs.mean-oldMean, 2));
            this.weightSum += obs.weightSum;
        }
    }

    public double getTotalWeightObserved() {
        return this.weightSum;
    }

    public double getMean() {
        return this.mean;
    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double getVariance() {
        return this.weightSum > 1.0 ? this.varianceSum / (this.weightSum - 1.0)
                : 0.0;
    }

    public double probabilityDensity(double value) {
        if (this.weightSum > 0.0) {
            double stdDev = getStdDev();
            if (stdDev > 0.0) {
                double diff = value - getMean();
                return (1.0 / (NORMAL_CONSTANT * stdDev))
                        * Math.exp(-(diff * diff / (2.0 * stdDev * stdDev)));
            }
            return value == getMean() ? 1.0 : 0.0;
        }
        return 0.0;
    }

    public double[] estimatedWeight_LessThan_EqualTo_GreaterThan_Value(
            double value) {
        double equalToWeight = probabilityDensity(value) * this.weightSum;
        double stdDev = getStdDev();
        double lessThanWeight = stdDev > 0.0 ? com.yahoo.labs.samoa.moa.core.Statistics.normalProbability((value - getMean()) / stdDev)
                * this.weightSum - equalToWeight
                : (value < getMean() ? this.weightSum - equalToWeight : 0.0);
        double greaterThanWeight = this.weightSum - equalToWeight
                - lessThanWeight;
        if (greaterThanWeight < 0.0) {
            greaterThanWeight = 0.0;
        }
        return new double[]{lessThanWeight, equalToWeight, greaterThanWeight};
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }
}
