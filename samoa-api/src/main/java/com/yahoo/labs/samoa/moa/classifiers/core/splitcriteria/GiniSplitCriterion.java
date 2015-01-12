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

import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.core.Utils;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Class for computing splitting criteria using Gini
 * with respect to distributions of class values.
 * The split criterion is used as a parameter on
 * decision trees and decision stumps.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class GiniSplitCriterion extends AbstractOptionHandler implements
        SplitCriterion {

    private static final long serialVersionUID = 1L;

    @Override
    public double getMeritOfSplit(double[] preSplitDist, double[][] postSplitDists) {
        double totalWeight = 0.0;
        double[] distWeights = new double[postSplitDists.length];
        for (int i = 0; i < postSplitDists.length; i++) {
            distWeights[i] = Utils.sum(postSplitDists[i]);
            totalWeight += distWeights[i];
        }
        double gini = 0.0;
        for (int i = 0; i < postSplitDists.length; i++) {
            gini += (distWeights[i] / totalWeight)
                    * computeGini(postSplitDists[i], distWeights[i]);
        }
        return 1.0 - gini;
    }

    @Override
    public double getRangeOfMerit(double[] preSplitDist) {
        return 1.0;
    }

    public static double computeGini(double[] dist, double distSumOfWeights) {
        double gini = 1.0;
        for (double aDist : dist) {
            double relFreq = aDist / distSumOfWeights;
            gini -= relFreq * relFreq;
        }
        return gini;
    }

    public static double computeGini(double[] dist) {
        return computeGini(dist, Utils.sum(dist));
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
