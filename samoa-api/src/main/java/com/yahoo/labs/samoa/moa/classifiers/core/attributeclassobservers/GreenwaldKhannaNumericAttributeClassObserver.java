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
import com.yahoo.labs.samoa.moa.classifiers.core.conditionaltests.NumericAttributeBinaryTest;
import com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria.SplitCriterion;
import com.yahoo.labs.samoa.moa.core.Utils;

import com.yahoo.labs.samoa.moa.core.AutoExpandVector;
import com.yahoo.labs.samoa.moa.core.DoubleVector;
import com.yahoo.labs.samoa.moa.core.GreenwaldKhannaQuantileSummary;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Class for observing the class data distribution for a numeric attribute using Greenwald and Khanna methodology.
 * This observer monitors the class distribution of a given attribute.
 * Used in naive Bayes and decision trees to monitor data statistics on leaves.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class GreenwaldKhannaNumericAttributeClassObserver extends AbstractOptionHandler implements NumericAttributeClassObserver {

    private static final long serialVersionUID = 1L;

    protected AutoExpandVector<GreenwaldKhannaQuantileSummary> attValDistPerClass = new AutoExpandVector<>();

    public IntOption numTuplesOption = new IntOption("numTuples", 'n',
        "The number of tuples.", 10, 1, Integer.MAX_VALUE);

    @Override
    public void observeAttributeClass(double attVal, int classVal, double weight) {
        if (!Utils.isMissingValue(attVal)) {
            GreenwaldKhannaQuantileSummary valDist = this.attValDistPerClass.get(classVal);
            if (valDist == null) {
                valDist = new GreenwaldKhannaQuantileSummary(this.numTuplesOption.getValue());
                this.attValDistPerClass.set(classVal, valDist);
            }
            // TODO: not taking weight into account
            valDist.insert(attVal);
        }
    }

    @Override
    public double probabilityOfAttributeValueGivenClass(double attVal,
            int classVal) {
        // TODO: NaiveBayes broken until implemented
        return 0.0;
    }

    @Override
    public AttributeSplitSuggestion getBestEvaluatedSplitSuggestion(
            SplitCriterion criterion, double[] preSplitDist, int attIndex,
            boolean binaryOnly) {
        AttributeSplitSuggestion bestSuggestion = null;
        for (GreenwaldKhannaQuantileSummary qs : this.attValDistPerClass) {
            if (qs != null) {
                double[] cutpoints = qs.getSuggestedCutpoints();
                for (double cutpoint : cutpoints) {
                    double[][] postSplitDists = getClassDistsResultingFromBinarySplit(cutpoint);
                    double merit = criterion.getMeritOfSplit(preSplitDist,
                            postSplitDists);
                    if ((bestSuggestion == null)
                            || (merit > bestSuggestion.merit)) {
                        bestSuggestion = new AttributeSplitSuggestion(
                                new NumericAttributeBinaryTest(attIndex,
                                cutpoint, true), postSplitDists, merit);
                    }
                }
            }
        }
        return bestSuggestion;
    }

    // assume all values equal to splitValue go to lhs
    public double[][] getClassDistsResultingFromBinarySplit(double splitValue) {
        DoubleVector lhsDist = new DoubleVector();
        DoubleVector rhsDist = new DoubleVector();
        for (int i = 0; i < this.attValDistPerClass.size(); i++) {
            GreenwaldKhannaQuantileSummary estimator = this.attValDistPerClass.get(i);
            if (estimator != null) {
                long countBelow = estimator.getCountBelow(splitValue);
                lhsDist.addToValue(i, countBelow);
                rhsDist.addToValue(i, estimator.getTotalCount() - countBelow);
            }
        }
        return new double[][]{lhsDist.getArrayRef(), rhsDist.getArrayRef()};
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
        // TODO Auto-generated method stub
    }

    @Override
    public void observeAttributeTarget(double attVal, double target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
