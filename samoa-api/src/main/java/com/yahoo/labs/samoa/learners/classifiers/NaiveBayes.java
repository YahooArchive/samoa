package com.yahoo.labs.samoa.learners.classifiers;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers.AttributeClassObserver;
import com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers.GaussianNumericAttributeClassObserver;
import com.yahoo.labs.samoa.moa.core.GaussianEstimator;

/**
 * Implementation of a non-distributed Naive Bayes classifier.
 * 
 * At the moment, the implementation models all attributes as numeric
 * attributes.
 * 
 * @author Olivier Van Laere (vanlaere yahoo-inc dot com)
 */
public class NaiveBayes implements LocalLearner {

	/**
	 * serialVersionUID for serialization
	 */
	private static final long serialVersionUID = 1325775209672996822L;

	/**
	 * Instance of a logger for use in this class.
	 */
	private static Logger log = LoggerFactory.getLogger(NaiveBayes.class);

	/**
	 * The actual model.
	 */
	protected Map<Integer, GaussianNumericAttributeClassObserver> attributeObservers;

	/**
	 * Class statistics
	 */
	protected Map<Integer, Double> classInstances;

	/**
	 * Retrieve the number of classes currently known to this local model
	 * 
	 * @return the number of classes currently known to this local model
	 */
	protected int getNumberOfClasses() {
		return this.classInstances.size();
	}

	/**
	 * Track training instances seen.
	 */
	protected long instancesSeen = 0L;

	/**
	 * Explicit no-arg constructor.
	 */
	public NaiveBayes() {
		// Init the model
		resetLearning();
	}

	/**
	 * Create an instance of this LocalLearner implementation.
	 */
	@Override
	public LocalLearner create() {
		return new NaiveBayes();
	}

	/**
	 * Predicts the class memberships for a given instance. If an instance is
	 * unclassified, the returned array elements will be all zero.
	 * 
	 * Smoothing is being implemented by the AttributeClassObserver classes. At
	 * the moment, the GaussianNumericProbabilityAttributeClassObserver needs no
	 * smoothing as it processes continuous variables.
	 * 
	 * Please note that we transform the scores to log space to avoid underflow,
	 * and we replace the multiplication with addition.
	 * 
	 * The resulting scores are no longer probabilities, as a mixture of
	 * probability densities and probabilities can be used in the computation.
	 * 
	 * @param inst
	 *            the instance to be classified
	 * @return an array containing the estimated membership scores of the test
	 *         instance in each class, in log space.
	 */
	@Override
	public double[] getVotesForInstance(Instance inst) {
		// Prepare the results array
		double[] votes = new double[getNumberOfClasses()];
		// Over all classes
		for (int classIndex = 0; classIndex < votes.length; classIndex++) {
			// Get the prior for this class
			votes[classIndex] = Math.log(getPrior(classIndex));
			// Get mass for the class
			Double classMass = this.classInstances.get(classIndex);
			for (Integer attributeID : attributeObservers.keySet()) { 
				// Skip class attribute
				if (attributeID == inst.classIndex())
					continue;
				// Get the observer for the given attribute
				GaussianNumericAttributeClassObserver obs = attributeObservers.get(attributeID);
				// Get the estimator
				GaussianEstimator estimator = obs.getEstimator(classIndex);
				// Get mass for the attribute
				Double attrMass = estimator.getTotalWeightObserved();
				// Compute the mass for zero attributes we must have seen
				Double zeroMass = classMass - attrMass;
				// Create a new empty Estimator
				GaussianEstimator zeroEstimator = new GaussianEstimator();
				// Add a zero value observation, but with the mass of all untracked zeros before
				zeroEstimator.addObservation(0, zeroMass);
				// Merge the existing estimator for the seen attribute for this class,
				// with the one for the previously ignored zero observations
				zeroEstimator.addObservations(estimator);				
				// Directly invoke probabilityDensity on the new estimator to get the value
				double value = zeroEstimator.probabilityDensity(inst.value(attributeID));
				// Back to adding to NB membership scores (in log space)
				votes[classIndex] += Math.log(value);
			}
		}
		return votes;
	}

	/**
	 * Compute the prior for the given classIndex.
	 * 
	 * Implemented by maximum likelihood at the moment.
	 * 
	 * @param classIndex
	 *            Id of the class for which we want to compute the prior.
	 * @return Prior probability for the requested class
	 */
	private double getPrior(int classIndex) {
		// Maximum likelihood
		Double currentCount = this.classInstances.get(classIndex);
		if (currentCount == null || currentCount == 0)
			return 0;
		else
			return currentCount * 1. / this.instancesSeen;
	}

	/**
	 * Resets this classifier. It must be similar to starting a new classifier
	 * from scratch.
	 */
	@Override
	public void resetLearning() {
		// Reset priors
		this.instancesSeen = 0L;
		this.classInstances = new HashMap<Integer, Double>();
		// Init the attribute observers
		this.attributeObservers = new HashMap<Integer, GaussianNumericAttributeClassObserver>();
	}

	/**
	 * Trains this classifier incrementally using the given instance.
	 * 
	 * @param inst
	 *            the instance to be used for training
	 */
	@Override
	public void trainOnInstance(Instance inst) {
		// Update class statistics with weights
		int classIndex = (int) inst.classValue();
		Double weight = this.classInstances.get(classIndex);
		if (weight == null)
			weight = 0.;
		this.classInstances.put(classIndex, weight + inst.weight());
		// Iterate over the attributes of the given instance
		for (int attributePosition = 0; attributePosition < inst
				.numAttributes(); attributePosition++) {
			// Get the attribute index - Dense -> 1:1, Sparse is remapped
			int attributeID = inst.index(attributePosition);
			// Skip class attribute
			if (attributeID == inst.classIndex())
				continue;
			// Get the attribute observer for the current attribute
			GaussianNumericAttributeClassObserver obs = this.attributeObservers
					.get(attributeID);
			// Lazy init of observers, if null, instantiate a new one
			if (obs == null) {
				// FIXME: At this point, we model everything as a numeric
				// attribute
				obs = new GaussianNumericAttributeClassObserver();
				this.attributeObservers.put(attributeID, obs);
			}
			// FIXME: Sanity check on data values, for now just learn
			// Learn attribute value for given class
			obs.observeAttributeClass(inst.valueSparse(attributePosition),
					(int) inst.classValue(), inst.weight());
		}
		// Count another training instance
		this.instancesSeen++;
	}

	@Override
	public void setDataset(Instances dataset) {
		// Do nothing
	}
}
