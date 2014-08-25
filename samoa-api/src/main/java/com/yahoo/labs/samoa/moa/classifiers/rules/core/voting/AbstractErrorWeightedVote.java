package com.yahoo.labs.samoa.moa.classifiers.rules.core.voting;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.moa.AbstractMOAObject;

/**
 * AbstractErrorWeightedVote class for weighted votes based on estimates of errors. 
 *
 * @author Joao Duarte (jmduarte@inescporto.pt)
 * @version $Revision: 1 $
 */
public abstract class AbstractErrorWeightedVote extends AbstractMOAObject implements ErrorWeightedVote{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340491298217227675L;
	protected List<double[]> votes;
	protected List<Double> errors;
	protected double[] weights;



	public AbstractErrorWeightedVote() {
		super();
		votes = new ArrayList<double[]>();
		errors = new ArrayList<Double>();
	}
	
	public AbstractErrorWeightedVote(AbstractErrorWeightedVote aewv) {
		super();
		votes = new ArrayList<double[]>();
		for (double[] vote:aewv.votes) {
			double[] v = new double[vote.length];
			for (int i=0; i<vote.length; i++) v[i] = vote[i];
			votes.add(v);
		}
		errors = new ArrayList<Double>();
		for (Double db:aewv.errors) {
			errors.add(db.doubleValue());
		}
		if (aewv.weights != null) {
			weights = new double[aewv.weights.length];
			for (int i = 0; i<aewv.weights.length; i++) 
				weights[i] = aewv.weights[i];
		}
	}


	@Override
	public void addVote(double [] vote, double error) {
		votes.add(vote);
		errors.add(error);
	}

	@Override
	abstract public double[] computeWeightedVote();

	@Override
	public double getWeightedError()
	{
		double weightedError=0;
		if (weights!=null && weights.length==errors.size())
		{
			for (int i=0; i<weights.length; ++i)
				weightedError+=errors.get(i)*weights[i];
		}
		else
			weightedError=-1;
		return weightedError;
	}

	@Override
	public double [] getWeights() {
		return weights;
	}

	@Override
	public int getNumberVotes() {
		return votes.size();
	}
}
