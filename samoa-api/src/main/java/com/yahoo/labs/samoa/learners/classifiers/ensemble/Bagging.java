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

import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.learners.classifiers.LocalClassifierProcessor;
import com.yahoo.labs.samoa.learners.classifiers.LocalClassifierAdapter;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.learners.classifiers.MOAClassifierAdapter;

/**
 * The Bagging Classifier by Oza and Russell.
 */
public class Bagging implements Learner , Configurable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2971850264864952099L;
	
	/** The base learner option. */
	public ClassOption baseLearnerOption = new ClassOption("baseLearner", 'l',
			"Classifier to train.", LocalClassifierAdapter.class, MOAClassifierAdapter.class.getName());

	/** The ensemble size option. */
	public IntOption ensembleSizeOption = new IntOption("ensembleSize", 's',
			"The number of models in the bag.", 10, 1, Integer.MAX_VALUE);

	/** The distributor pi. */
	private ProcessingItem distributorPI;
	
	/** The learner pi. */
	private ProcessingItem learnerPI;
	
	/** The prediction combiner pi. */
	private ProcessingItem predictionCombinerPI;
	
	/** The training stream. */
	private Stream trainingStream;
	
	/** The prediction stream. */
	private Stream predictionStream;
	
	/** The result stream. */
	protected Stream resultStream;
	
	/** The dataset. */
	private Instances dataset;

	/**
	 * Sets the layout.
	 */
	protected void setLayout() {

		int sizeEnsemble = this.ensembleSizeOption.getValue();

		BaggingDistributorProcessor distributorP = new BaggingDistributorProcessor();
		distributorP.setSizeEnsemble(sizeEnsemble);
		distributorPI = this.builder.createPi(distributorP, 1);
	
		LocalClassifierProcessor learnerP = new LocalClassifierProcessor();
                LocalClassifierAdapter learner = (LocalClassifierAdapter) this.baseLearnerOption.getValue();
                learner.setDataset(this.dataset);
		learnerP.setClassifier(learner);
		learnerPI = this.builder.createPi(learnerP, sizeEnsemble);
		
		PredictionCombinerProcessor predictionCombinerP= new PredictionCombinerProcessor();
		predictionCombinerP.setSizeEnsemble(sizeEnsemble);
		predictionCombinerPI = this.builder.createPi(predictionCombinerP, 1);
		
		//Streams
		resultStream = this.builder.createStream(predictionCombinerPI);
		predictionCombinerP.setOutputStream(resultStream);

		Stream toPredictionCombinerStream = this.builder.createStream(learnerPI);
		predictionCombinerPI.connectInputKeyStream(toPredictionCombinerStream);
		
		trainingStream = this.builder.createStream(distributorPI);
		learnerPI.connectInputKeyStream(trainingStream);
	
		predictionStream = this.builder.createStream(distributorPI);		
		learnerPI.connectInputKeyStream(predictionStream);
		
		distributorP.setTrainingStream(trainingStream);
		distributorP.setPredictionStream(predictionStream);

		learnerP.setOutputStream(toPredictionCombinerStream);
	}

	/** The builder. */
	private TopologyBuilder builder;

	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#init(samoa.engines.Engine, samoa.core.Stream, weka.core.Instances)
	 */			
	
	@Override
	public void init(TopologyBuilder builder, Instances dataset) {
		this.builder = builder;
		this.dataset = dataset;
		this.setLayout();
	}

	/* (non-Javadoc)
	 * @see moa.MOAObject#getDescription(java.lang.StringBuilder, int)
	 */
//	@Override
//	public void getDescription(StringBuilder arg0, int arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see moa.options.AbstractOptionHandler#prepareForUseImpl(moa.tasks.TaskMonitor, moa.core.ObjectRepository)
//	 */
//	@Override
//	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
//		// TODO Auto-generated method stub
//
//	}

	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#getInputProcessingItem()
	 */
	@Override
	public ProcessingItem getInputProcessingItem() {
		return distributorPI;
	}

	/* (non-Javadoc)
	 * @see samoa.classifiers.Classifier#getResultStream()
	 */
	@Override
	public Stream getResultStream() {
		return this.resultStream;
	}
}
