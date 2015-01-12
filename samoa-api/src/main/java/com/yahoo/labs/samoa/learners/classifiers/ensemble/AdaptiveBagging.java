package com.yahoo.labs.samoa.learners.classifiers.ensemble;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.AdaptiveLearner;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.learners.classifiers.trees.VerticalHoeffdingTree;
import com.yahoo.labs.samoa.moa.classifiers.core.driftdetection.ADWINChangeDetector;
import com.yahoo.labs.samoa.moa.classifiers.core.driftdetection.ChangeDetector;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

/**
 * The Bagging Classifier by Oza and Russell.
 */
public class AdaptiveBagging implements Learner, Configurable {
    
	/** Logger */
  private static final Logger logger = LoggerFactory.getLogger(AdaptiveBagging.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2971850264864952099L;
	
	/** The base learner option. */
	public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
			"Classifier to train.", Learner.class, VerticalHoeffdingTree.class.getName());

	/** The ensemble size option. */
	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's',
			"The number of models in the bag.", 10, 1, Integer.MAX_VALUE);

	public ClassOption driftDetectionMethodOption = new ClassOption("driftDetectionMethod", 'd',
      "Drift detection method to use.", ChangeDetector.class, ADWINChangeDetector.class.getName());

	/** The distributor processor. */
	private BaggingDistributorProcessor distributorP;

	/** The result stream. */
	protected Stream resultStream;
	
	/** The dataset. */
	private Instances dataset;
        
	protected Learner classifier;
        
  protected int parallelism;

	/**
	 * Sets the layout.
	 */
	protected void setLayout() {

		int sizeEnsemble = this.ensembleSizeOption.getValue();

		distributorP = new BaggingDistributorProcessor();
		distributorP.setSizeEnsemble(sizeEnsemble);
                this.builder.addProcessor(distributorP, 1);
		        
		//instantiate classifier
		classifier = this.baseLearnerOption.getValue();
		if (classifier instanceof AdaptiveLearner) {
				// logger.info("Building an AdaptiveLearner {}", classifier.getClass().getName());
				AdaptiveLearner ada = (AdaptiveLearner) classifier;
				ada.setChangeDetector((ChangeDetector) this.driftDetectionMethodOption.getValue());
		}
		classifier.init(builder, this.dataset, sizeEnsemble);
        
		PredictionCombinerProcessor predictionCombinerP= new PredictionCombinerProcessor();
		predictionCombinerP.setSizeEnsemble(sizeEnsemble);
		this.builder.addProcessor(predictionCombinerP, 1);
		
		//Streams
		resultStream = this.builder.createStream(predictionCombinerP);
		predictionCombinerP.setOutputStream(resultStream);

		for (Stream subResultStream:classifier.getResultStreams()) {
			this.builder.connectInputKeyStream(subResultStream, predictionCombinerP);
		}
		
		/* The training stream. */
		Stream testingStream = this.builder.createStream(distributorP);
                this.builder.connectInputKeyStream(testingStream, classifier.getInputProcessor());
	
		/* The prediction stream. */
		Stream predictionStream = this.builder.createStream(distributorP);
                this.builder.connectInputKeyStream(predictionStream, classifier.getInputProcessor());
		
		distributorP.setOutputStream(testingStream);
		distributorP.setPredictionStream(predictionStream);
	}

	/** The builder. */
	private TopologyBuilder builder;
		
	
	@Override
	public void init(TopologyBuilder builder, Instances dataset, int parallelism) {
		this.builder = builder;
		this.dataset = dataset;
                this.parallelism = parallelism;
		this.setLayout();
	}

        @Override
	public Processor getInputProcessor() {
		return distributorP;
	}
        
	/* (non-Javadoc)
	 * @see samoa.learners.Learner#getResultStreams()
	 */
	@Override
	public Set<Stream> getResultStreams() {
		return ImmutableSet.of(this.resultStream);
	}
}
