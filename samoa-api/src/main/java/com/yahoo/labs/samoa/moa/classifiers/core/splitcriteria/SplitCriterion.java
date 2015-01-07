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

import com.yahoo.labs.samoa.moa.options.OptionHandler;

/**
 * Interface for computing splitting criteria.
 * with respect to distributions of class values.
 * The split criterion is used as a parameter on
 * decision trees and decision stumps.
 * The two split criteria most used are 
 * Information Gain and Gini. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface SplitCriterion extends OptionHandler {

    /**
     * Computes the merit of splitting for a given
     * ditribution before the split and after it.
     *
     * @param preSplitDist the class distribution before the split
     * @param postSplitDists the class distribution after the split
     * @return value of the merit of splitting
     */
    public double getMeritOfSplit(double[] preSplitDist,
            double[][] postSplitDists);

    /**
     * Computes the range of splitting merit
     *
     * @param preSplitDist the class distribution before the split
     * @return value of the range of splitting merit
     */
    public double getRangeOfMerit(double[] preSplitDist);
}
