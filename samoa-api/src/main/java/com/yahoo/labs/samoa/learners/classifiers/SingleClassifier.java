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

/**
 * License
 */

import com.google.common.collect.ImmutableSet;
import java.util.Set;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.AdaptiveLearner;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.moa.classifiers.core.driftdetection.ChangeDetector;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;
/**
 * 
 * Classifier that contain a single classifier.
 * 
 */
public final class SingleClassifier implements Learner, AdaptiveLearner, Configurable {

	private static final long serialVersionUID = 684111382631697031L;
	
	private LocalLearnerProcessor learnerP;
		
	private Stream resultStream;

	private Instances dataset;

	public ClassOption learnerOption = new ClassOption("learner", 'l',
			"Classifier to train.", LocalLearner.class, SimpleClassifierAdapter.class.getName());
	
	private TopologyBuilder builder;

	private int parallelism;


	@Override
	public void init(TopologyBuilder builder, Instances dataset, int parallelism){
		this.builder = builder;
		this.dataset = dataset;
		this.parallelism = parallelism;
		this.setLayout();
	}


	protected void setLayout() {		
		learnerP = new LocalLearnerProcessor();
		learnerP.setChangeDetector(this.getChangeDetector());
		LocalLearner learner = this.learnerOption.getValue();
		learner.setDataset(this.dataset);
		learnerP.setLearner(learner);
                
		//learnerPI = this.builder.createPi(learnerP, 1);
		this.builder.addProcessor(learnerP, parallelism);
		resultStream = this.builder.createStream(learnerP);

		learnerP.setOutputStream(resultStream);
	}

	@Override
	public Processor getInputProcessor() {
		return learnerP;
	}

	/* (non-Javadoc)
	 * @see samoa.learners.Learner#getResultStreams()
	 */
	@Override
	public Set<Stream> getResultStreams() {
		return ImmutableSet.of(this.resultStream);
	}

	protected ChangeDetector changeDetector;    

	@Override
	public ChangeDetector getChangeDetector() {
		return this.changeDetector;
	}

	@Override
	public void setChangeDetector(ChangeDetector cd) {
		this.changeDetector = cd;
	}
}
