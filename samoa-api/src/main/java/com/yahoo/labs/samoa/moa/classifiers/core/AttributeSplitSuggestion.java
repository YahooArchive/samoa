package com.yahoo.labs.samoa.moa.classifiers.core;

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
import com.yahoo.labs.samoa.moa.classifiers.core.conditionaltests.InstanceConditionalTest;

/**
 * Class for computing attribute split suggestions given a split test.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class AttributeSplitSuggestion extends AbstractMOAObject implements Comparable<AttributeSplitSuggestion> {
    
    private static final long serialVersionUID = 1L;

    public InstanceConditionalTest splitTest;

    public double[][] resultingClassDistributions;

    public double merit;
    
    public AttributeSplitSuggestion() {}

    public AttributeSplitSuggestion(InstanceConditionalTest splitTest,
            double[][] resultingClassDistributions, double merit) {
        this.splitTest = splitTest;
        this.resultingClassDistributions = resultingClassDistributions.clone();
        this.merit = merit;
    }

    public int numSplits() {
        return this.resultingClassDistributions.length;
    }

    public double[] resultingClassDistributionFromSplit(int splitIndex) {
        return this.resultingClassDistributions[splitIndex].clone();
    }

    @Override
    public int compareTo(AttributeSplitSuggestion comp) {
        return Double.compare(this.merit, comp.merit);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // do nothing
    }
}
