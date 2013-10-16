package com.yahoo.labs.samoa.moa.classifiers.functions;

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

import com.yahoo.labs.samoa.moa.classifiers.AbstractClassifier;
import com.yahoo.labs.samoa.moa.core.DoubleVector;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.moa.core.StringUtils;
import com.yahoo.labs.samoa.instances.Instance;

/**
 * Majority class learner. This is the simplest classifier.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class MajorityClass extends AbstractClassifier {

    private static final long serialVersionUID = 1L;

    @Override
    public String getPurposeString() {
        return "Majority class classifier: always predicts the class that has been observed most frequently the in the training data.";
    }

    protected DoubleVector observedClassDistribution;

    @Override
    public void resetLearningImpl() {
        this.observedClassDistribution = new DoubleVector();
    }

    @Override
    public void trainOnInstanceImpl(Instance inst) {
        this.observedClassDistribution.addToValue((int) inst.classValue(), inst.weight());
    }

    public double[] getVotesForInstance(Instance i) {
        return this.observedClassDistribution.getArrayCopy();
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        return null;
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {
        StringUtils.appendIndented(out, indent, "Predicted majority ");
        out.append(getClassNameString());
        out.append(" = ");
        out.append(getClassLabelString(this.observedClassDistribution.maxIndex()));
        StringUtils.appendNewline(out);
        for (int i = 0; i < this.observedClassDistribution.numValues(); i++) {
            StringUtils.appendIndented(out, indent, "Observed weight of ");
            out.append(getClassLabelString(i));
            out.append(": ");
            out.append(this.observedClassDistribution.getValue(i));
            StringUtils.appendNewline(out);
        }
    }

    public boolean isRandomizable() {
        return false;
    }
}
