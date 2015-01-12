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

import com.yahoo.labs.samoa.moa.core.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.yahoo.labs.samoa.moa.classifiers.core.AttributeSplitSuggestion;
import com.yahoo.labs.samoa.moa.classifiers.core.conditionaltests.NumericAttributeBinaryTest;
import com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria.SplitCriterion;

import com.yahoo.labs.samoa.moa.core.DoubleVector;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Class for observing the class data distribution for a numeric attribute as in VFML.
 * Used in naive Bayes and decision trees to monitor data statistics on leaves.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class VFMLNumericAttributeClassObserver extends AbstractOptionHandler implements NumericAttributeClassObserver {

    private static final long serialVersionUID = 1L;

    @Override
    public void observeAttributeTarget(double attVal, double target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected class Bin implements Serializable {

        private static final long serialVersionUID = 1L;

        public double lowerBound, upperBound;

        public DoubleVector classWeights = new DoubleVector();

        public int boundaryClass;

        public double boundaryWeight;
    }

    protected List<Bin> binList = new ArrayList<>();

    public IntOption numBinsOption = new IntOption("numBins", 'n',
        "The number of bins.", 10, 1, Integer.MAX_VALUE);


    @Override
    public void observeAttributeClass(double attVal, int classVal, double weight) {
        if (!Utils.isMissingValue(attVal)) {
            if (this.binList.size() < 1) {
                // create the first bin
                Bin newBin = new Bin();
                newBin.classWeights.addToValue(classVal, weight);
                newBin.boundaryClass = classVal;
                newBin.boundaryWeight = weight;
                newBin.upperBound = attVal;
                newBin.lowerBound = attVal;
                this.binList.add(newBin);
            } else {
                // find bin containing new example with binary search
                int index = 0;
                boolean found = false;
                int min = 0;
                int max = this.binList.size() - 1;
                while ((min <= max) && !found) {
                    int i = (min + max) / 2;
                    Bin bin = this.binList.get(i);
                    if (((attVal >= bin.lowerBound) && (attVal < bin.upperBound))
                            || ((i == this.binList.size() - 1)
                            && (attVal >= bin.lowerBound) && (attVal <= bin.upperBound))) {
                        found = true;
                        index = i;
                    } else if (attVal < bin.lowerBound) {
                        max = i - 1;
                    } else {
                        min = i + 1;
                    }
                }
                boolean first = false;
                boolean last = false;
                if (!found) {
                    // determine if it is before or after the existing range
                    Bin bin = this.binList.get(0);
                    if (bin.lowerBound > attVal) {
                        // go before the first bin
                        index = 0;
                        first = true;
                    } else {
                        // if we haven't found it yet value must be > last bins
                        // upperBound
                        index = this.binList.size() - 1;
                        last = true;
                    }
                }
                Bin bin = this.binList.get(index); // VLIndex(ct->bins, index);
                if ((bin.lowerBound == attVal)
                        || (this.binList.size() >= this.numBinsOption.getValue())) {// Option.getValue())
                    // {//1000)
                    // {
                    // if this is the exact same boundary and class as the bin
                    // boundary or we aren't adding new bins any more then
                    // increment
                    // boundary counts
                    bin.classWeights.addToValue(classVal, weight);
                    if ((bin.boundaryClass == classVal)
                            && (bin.lowerBound == attVal)) {
                        // if it is also the same class then special case it
                        bin.boundaryWeight += weight;
                    }
                } else {
                    // create a new bin
                    Bin newBin = new Bin();
                    newBin.classWeights.addToValue(classVal, weight);
                    newBin.boundaryWeight = weight;
                    newBin.boundaryClass = classVal;
                    newBin.upperBound = bin.upperBound;
                    newBin.lowerBound = attVal;

                    double percent = 0.0;
                    // estimate initial counts with a linear interpolation
                    if (!((bin.upperBound - bin.lowerBound == 0) || last || first)) {
                        percent = 1.0 - ((attVal - bin.lowerBound) / (bin.upperBound - bin.lowerBound));
                    }

                    // take out the boundry points, they stay with the old bin
                    bin.classWeights.addToValue(bin.boundaryClass,
                            -bin.boundaryWeight);
                    DoubleVector weightToShift = new DoubleVector(
                            bin.classWeights);
                    weightToShift.scaleValues(percent);
                    newBin.classWeights.addValues(weightToShift);
                    bin.classWeights.subtractValues(weightToShift);
                    // put the boundry examples back in
                    bin.classWeights.addToValue(bin.boundaryClass,
                            bin.boundaryWeight);

                    // insert the new bin in the right place
                    if (last) {
                        bin.upperBound = attVal;
                        newBin.upperBound = attVal;
                        this.binList.add(newBin);
                    } else if (first) {
                        newBin.upperBound = bin.lowerBound;
                        this.binList.add(0, newBin);
                    } else {
                        newBin.upperBound = bin.upperBound;
                        bin.upperBound = attVal;
                        this.binList.add(index + 1, newBin);
                    }
                }
            }
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
        DoubleVector rightDist = new DoubleVector();
        for (Bin bin : this.binList) {
            rightDist.addValues(bin.classWeights);
        }
        DoubleVector leftDist = new DoubleVector();
        for (Bin bin : this.binList) {
            leftDist.addValues(bin.classWeights);
            rightDist.subtractValues(bin.classWeights);
            double[][] postSplitDists = new double[][]{
                leftDist.getArrayCopy(), rightDist.getArrayCopy()};
            double merit = criterion.getMeritOfSplit(preSplitDist,
                    postSplitDists);
            if ((bestSuggestion == null) || (merit > bestSuggestion.merit)) {
                bestSuggestion = new AttributeSplitSuggestion(
                        new NumericAttributeBinaryTest(attIndex,
                        bin.upperBound, false), postSplitDists, merit);
            }
        }
        return bestSuggestion;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
        // TODO Auto-generated method stub
    }
}
