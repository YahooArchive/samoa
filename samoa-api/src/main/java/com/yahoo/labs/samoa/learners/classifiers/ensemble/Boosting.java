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

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.learners.classifiers.LocalClassifierAdapter;
import com.yahoo.labs.samoa.learners.classifiers.LocalClassifierProcessor;
import com.yahoo.labs.samoa.learners.classifiers.MOAClassifierAdapter;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

/**
 * The Bagging Classifier by Oza and Russell.
 */
public class Boosting implements Learner , Configurable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2971850264864952099L;
	
	/** The base learner option. */
	public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
			"Classifier to train.", LocalClassifierAdapter.class, MOAClassifierAdapter.class.getName());

	/** The ensemble size option. */
	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's',
			"The number of models in the bag.", 10, 1, Integer.MAX_VALUE);

	/** The distributor processor. */
	private BoostingDistributorProcessor distributorP;
                
	/** The training stream. */
	private Stream trainingStream;
        
        /** The testing stream. */
	private Stream testingStream;
	
	/** The prediction stream. */
	private Stream predictionStream;
	
	/** The result stream. */
	protected Stream resultStream;
	
	/** The dataset. */
	private Instances dataset;
        
        protected int parallelism;

	/**
	 * Sets the layout.
	 */
	protected void setLayout() {

		int sizeEnsemble = this.ensembleSizeOption.getValue();

		distributorP = new BoostingDistributorProcessor();
		distributorP.setSizeEnsemble(sizeEnsemble);
                this.builder.addProcessor(distributorP, 1);
	
		LocalClassifierProcessor learnerP = new LocalClassifierProcessor();
                LocalClassifierAdapter learner = (LocalClassifierAdapter) this.baseLearnerOption.getValue();
                learner.setDataset(this.dataset);
		learnerP.setClassifier(learner);
                this.builder.addProcessor(learnerP, sizeEnsemble);
		
		BoostingPredictionCombinerProcessor predictionCombinerP= new BoostingPredictionCombinerProcessor();
		predictionCombinerP.setSizeEnsemble(sizeEnsemble);
		this.builder.addProcessor(predictionCombinerP, 1);
		
		//Streams
		resultStream = this.builder.createStream(predictionCombinerP);
		predictionCombinerP.setOutputStream(resultStream);

		Stream toPredictionCombinerStream = this.builder.createStream(learnerP);
		this.builder.connectInputKeyStream(toPredictionCombinerStream, predictionCombinerP);
		
		testingStream = this.builder.createStream(distributorP);
                this.builder.connectInputKeyStream(testingStream, learnerP);
	
		predictionStream = this.builder.createStream(distributorP);		
                this.builder.connectInputKeyStream(predictionStream, learnerP);
		
		distributorP.setOutputStream(testingStream);
		distributorP.setPredictionStream(predictionStream);

		learnerP.setOutputStream(toPredictionCombinerStream);
                
                // Addition to Bagging: stream to train
                trainingStream = this.builder.createStream(predictionCombinerP);
                predictionCombinerP.setTrainingStream(trainingStream);
                this.builder.connectInputKeyStream(trainingStream, learnerP);
                
	}

	/** The builder. */
	private TopologyBuilder builder;

	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#init(samoa.engines.Engine, samoa.core.Stream, weka.core.Instances)
	 */			
	
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
	 * @see samoa.classifiers.Classifier#getResultStream()
	 */
	@Override
	public Stream getResultStream() {
		return this.resultStream;
	}
}
