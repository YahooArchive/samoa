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
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Drift detection method based in EWMA Charts of Ross, Adams, Tasoulis and Hand
 * 2012
 *
 *
 * @author Manuel Baena (mbaena@lcc.uma.es)
 * @version $Revision: 7 $
 */
public class EWMAChartDM extends AbstractChangeDetector {

    private static final long serialVersionUID = -3518369648142099719L;

    //private static final int DDM_MIN_NUM_INST = 30;
    public IntOption minNumInstancesOption = new IntOption(
            "minNumInstances",
            'n',
            "The minimum number of instances before permitting detecting change.",
            30, 0, Integer.MAX_VALUE);

    public FloatOption lambdaOption = new FloatOption("lambda", 'l',
            "Lambda parameter of the EWMA Chart Method", 0.2, 0.0, Float.MAX_VALUE);

    private double m_n;

    private double m_sum;
    
    private double m_p;
    
    private double m_s;
    
    private double lambda;
    
    private double z_t;

    public EWMAChartDM() {
        resetLearning();
    }

    @Override
    public void resetLearning() {
        m_n = 1.0;
        m_sum = 0.0;
        m_p = 0.0;
        m_s = 0.0;
        z_t = 0.0;
        lambda = this.lambdaOption.getValue();
    }

    @Override
    public void input(double prediction) {
        // prediction must be 1 or 0
        // It monitors the error rate
        if (this.isChangeDetected) {
            resetLearning();
        }

        m_sum += prediction;
        
        m_p = m_sum/m_n; // m_p + (prediction - m_p) / (double) (m_n+1);

        m_s = Math.sqrt(  m_p * (1.0 - m_p)* lambda * (1.0 - Math.pow(1.0 - lambda, 2.0 * m_n)) / (2.0 - lambda));

        m_n++;

        z_t += lambda * (prediction - z_t);

        double L_t = 3.97 - 6.56 * m_p + 48.73 * Math.pow(m_p, 3) - 330.13 * Math.pow(m_p, 5) + 848.18 * Math.pow(m_p, 7); //%1 FP
        this.estimation = m_p;
        this.isChangeDetected = false;
        this.isWarningZone = false;
        this.delay = 0;

        if (m_n < this.minNumInstancesOption.getValue()) {
            return;
        }
            
        if (m_n > this.minNumInstancesOption.getValue() && z_t > m_p + L_t * m_s) {
            this.isChangeDetected = true;
            //resetLearning();
        } else {
            this.isWarningZone = z_t > m_p + 0.5 * L_t * m_s;
        }
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