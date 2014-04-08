package com.yahoo.labs.samoa.moa.learners;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
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

import com.yahoo.labs.samoa.moa.MOAObject;
import com.yahoo.labs.samoa.moa.core.Example;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.moa.options.OptionHandler;

/**
 * Learner interface for incremental learning models. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public interface Learner<E extends Example> extends MOAObject, OptionHandler {


    /**
     * Gets whether this learner needs a random seed.
     * Examples of methods that needs a random seed are bagging and boosting.
     *
     * @return true if the learner needs a random seed.
     */
    public boolean isRandomizable();

    /**
     * Sets the seed for random number generation.
     *
     * @param s the seed
     */
    public void setRandomSeed(int s);

    /**
     * Gets whether training has started.
     *
     * @return true if training has started
     */
    public boolean trainingHasStarted();

    /**
     * Gets the sum of the weights of the instances that have been used
     * by this learner during the training in <code>trainOnInstance</code>
     *
     * @return the weight of the instances that have been used training
     */
    public double trainingWeightSeenByModel();

    /**
     * Resets this learner. It must be similar to
     * starting a new learner from scratch.
     *
     */
    public void resetLearning();

    /**
     * Trains this learner incrementally using the given example.
     *
     * @param inst the instance to be used for training
     */
    public void trainOnInstance(E example);

    /**
     * Predicts the class memberships for a given instance. If
     * an instance is unclassified, the returned array elements
     * must be all zero.
     *
     * @param inst the instance to be classified
     * @return an array containing the estimated membership
     * probabilities of the test instance in each class
     */
    public double[] getVotesForInstance(E example);

    /**
     * Gets the current measurements of this learner.
     *
     * @return an array of measurements to be used in evaluation tasks
     */
    public Measurement[] getModelMeasurements();

    /**
     * Gets the learners of this ensemble.
     * Returns null if this learner is a single learner.
     *
     * @return an array of the learners of the ensemble
     */
    public Learner[] getSublearners();

     /**
     * Gets the model if this learner.
     *
     * @return the copy of this learner
     */
    public MOAObject getModel();
    
     /**
     * Sets the reference to the header of the data stream.
     * The header of the data stream is extended from WEKA <code>Instances</code>.
     * This header is needed to know the number of classes and attributes
     *
     * @param ih the reference to the data stream header
     */
    public void setModelContext(InstancesHeader ih);
    
    /**
     * Gets the reference to the header of the data stream.
     * The header of the data stream is extended from WEKA <code>Instances</code>.
     * This header is needed to know the number of classes and attributes
     *
     * @return the reference to the data stream header
     */
    public InstancesHeader getModelContext();
    
}



