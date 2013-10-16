package com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers;

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

import com.yahoo.labs.samoa.moa.classifiers.core.AttributeSplitSuggestion;
import com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria.SplitCriterion;
import com.yahoo.labs.samoa.moa.options.OptionHandler;

/**
 * Interface for observing the class data distribution for an attribute.
 * This observer monitors the class distribution of a given attribute.
 * Used in naive Bayes and decision trees to monitor data statistics on leaves.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface AttributeClassObserver extends OptionHandler {

    /**
     * Updates statistics of this observer given an attribute value, a class
     * and the weight of the instance observed
     *
     * @param attVal the value of the attribute
     * @param classVal the class
     * @param weight the weight of the instance
     */
    public void observeAttributeClass(double attVal, int classVal, double weight);

    /**
     * Gets the probability for an attribute value given a class
     *
     * @param attVal the attribute value
     * @param classVal the class
     * @return probability for an attribute value given a class
     */
    public double probabilityOfAttributeValueGivenClass(double attVal,
            int classVal);

    /**
     * Gets the best split suggestion given a criterion and a class distribution
     *
     * @param criterion the split criterion to use
     * @param preSplitDist the class distribution before the split
     * @param attIndex the attribute index
     * @param binaryOnly true to use binary splits
     * @return suggestion of best attribute split
     */
    public AttributeSplitSuggestion getBestEvaluatedSplitSuggestion(
            SplitCriterion criterion, double[] preSplitDist, int attIndex,
            boolean binaryOnly);


    public void observeAttributeTarget(double attVal, double target);
    
}
