package com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
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

import com.yahoo.labs.samoa.moa.core.Utils;

/**
 * Class for computing splitting criteria using information gain with respect to
 * distributions of class values for Multilabel data. The split criterion is
 * used as a parameter on decision trees and decision stumps.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author Jesse Read (jesse@tsc.uc3m.es)
 * @version $Revision: 1 $
 */
public class InfoGainSplitCriterionMultilabel extends InfoGainSplitCriterion {

    private static final long serialVersionUID = 1L;

    public static double computeEntropy(double[] dist) {
        double entropy = 0.0;
        double sum = 0.0;
        for (double d : dist) {
            sum += d;
        }
        if (sum > 0.0) {
            for (double num : dist) {
                double d = num / sum;
                if (d > 0.0) { // TODO: how small can d be before log2 overflows?
                    entropy -= d * Utils.log2(d) + (1 - d) * Utils.log2(1 - d); //Extension to Multilabel
                }
            }
        }
        return sum > 0.0 ? entropy : 0.0;
    }
}
