
package com.yahoo.labs.samoa.moa.classifiers.core.driftdetection;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;


/**
 * Drift detection method based in ADWIN. ADaptive sliding WINdow is a change
 * detector and estimator. It keeps a variable-length window of recently seen
 * items, with the property that the window has the maximal length statistically
 * consistent with the hypothesis "there has been no change in the average value
 * inside the window".
 *
 *
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class ADWINChangeDetector extends AbstractChangeDetector {

    protected ADWIN adwin;

    public FloatOption deltaAdwinOption = new FloatOption("deltaAdwin", 'a',
            "Delta of Adwin change detection", 0.002, 0.0, 1.0);

    @Override
    public void input(double inputValue) {
        if (this.adwin == null) {
            resetLearning();
        }
        this.isChangeDetected = adwin.setInput(inputValue);
        this.isWarningZone = false;
        this.delay = 0.0;
        this.estimation = adwin.getEstimation();
    }

    @Override
    public void resetLearning() {
        adwin = new ADWIN(this.deltaAdwinOption.getValue());
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
