package com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria;

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

import com.github.javacliparser.FloatOption;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.core.Utils;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Class for computing splitting criteria using information gain
 * with respect to distributions of class values.
 * The split criterion is used as a parameter on
 * decision trees and decision stumps.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class InfoGainSplitCriterion extends AbstractOptionHandler implements
        SplitCriterion {

    private static final long serialVersionUID = 1L;

    public FloatOption minBranchFracOption = new FloatOption("minBranchFrac",
            'f',
            "Minimum fraction of weight required down at least two branches.",
            0.01, 0.0, 0.5);

    @Override
    public double getMeritOfSplit(double[] preSplitDist,
            double[][] postSplitDists) {
        if (numSubsetsGreaterThanFrac(postSplitDists, this.minBranchFracOption.getValue()) < 2) {
            return Double.NEGATIVE_INFINITY;
        }
        return computeEntropy(preSplitDist) - computeEntropy(postSplitDists);
    }

    @Override
    public double getRangeOfMerit(double[] preSplitDist) {
        int numClasses = preSplitDist.length > 2 ? preSplitDist.length : 2;
        return Utils.log2(numClasses);
    }

    public static double computeEntropy(double[] dist) {
        double entropy = 0.0;
        double sum = 0.0;
        for (double d : dist) {
            if (d > 0.0) { // TODO: how small can d be before log2 overflows?
                entropy -= d * Utils.log2(d);
                sum += d;
            }
        }
        return sum > 0.0 ? (entropy + sum * Utils.log2(sum)) / sum : 0.0;
    }

    public static double computeEntropy(double[][] dists) {
        double totalWeight = 0.0;
        double[] distWeights = new double[dists.length];
        for (int i = 0; i < dists.length; i++) {
            distWeights[i] = Utils.sum(dists[i]);
            totalWeight += distWeights[i];
        }
        double entropy = 0.0;
        for (int i = 0; i < dists.length; i++) {
            entropy += distWeights[i] * computeEntropy(dists[i]);
        }
        return entropy / totalWeight;
    }

    public static int numSubsetsGreaterThanFrac(double[][] distributions, double minFrac) {
        double totalWeight = 0.0;
        double[] distSums = new double[distributions.length];
        for (int i = 0; i < distSums.length; i++) {
            for (int j = 0; j < distributions[i].length; j++) {
                distSums[i] += distributions[i][j];
            }
            totalWeight += distSums[i];
        }
        int numGreater = 0;
        for (double d : distSums) {
            double frac = d / totalWeight;
            if (frac > minFrac) {
                numGreater++;
            }
        }
        return numGreater;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        // TODO Auto-generated method stub
    }
}
