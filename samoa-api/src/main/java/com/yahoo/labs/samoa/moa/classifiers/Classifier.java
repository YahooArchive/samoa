package com.yahoo.labs.samoa.moa.classifiers;

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

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.moa.core.Example;
import com.yahoo.labs.samoa.moa.learners.Learner;

/**
 * Classifier interface for incremental classification models.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public interface Classifier extends Learner<Example<Instance>> {

    /**
     * Gets the classifiers of this ensemble. Returns null if this learner is a
     * single learner.
     *
     * @return an array of the learners of the ensemble
     */
    public Classifier[] getSubClassifiers();

    /**
     * Produces a copy of this learner.
     *
     * @return the copy of this learner
     */
    public Classifier copy();

    /**
     * Gets whether this classifier correctly classifies an instance. Uses
     * getVotesForInstance to obtain the prediction and the instance to obtain
     * its true class.
     *
     *
     * @param inst the instance to be classified
     * @return true if the instance is correctly classified
     */
    public boolean correctlyClassifies(Instance inst);

    /**
     * Trains this learner incrementally using the given example.
     *
     * @param inst the instance to be used for training
     */
    public void trainOnInstance(Instance inst);

    /**
     * Predicts the class memberships for a given instance. If an instance is
     * unclassified, the returned array elements must be all zero.
     *
     * @param inst the instance to be classified
     * @return an array containing the estimated membership probabilities of the
     * test instance in each class
     */
    public double[] getVotesForInstance(Instance inst);
}
