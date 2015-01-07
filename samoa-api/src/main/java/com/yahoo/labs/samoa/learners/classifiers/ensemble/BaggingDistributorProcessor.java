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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.moa.core.MiscUtils;
import com.yahoo.labs.samoa.topology.Stream;
import java.util.Random;

/**
 * The Class BaggingDistributorPE.
 */
public class BaggingDistributorProcessor implements Processor{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550901409625192730L;

	/** The size ensemble. */
	private int sizeEnsemble;
	
	/** The training stream. */
	private Stream trainingStream;
	
	/** The prediction stream. */
	private Stream predictionStream;

	/**
	 * On event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean process(ContentEvent event) {
		InstanceContentEvent inEvent = (InstanceContentEvent) event; //((s4Event) event).getContentEvent();
		//InstanceEvent inEvent = (InstanceEvent) event;

		if (inEvent.getInstanceIndex() < 0) {
			// End learning
			predictionStream.put(event);
			return false;
		}


                if (inEvent.isTesting()){ 
			Instance trainInst = inEvent.getInstance();
			for (int i = 0; i < sizeEnsemble; i++) {
				Instance weightedInst = trainInst.copy();
				//weightedInst.setWeight(trainInst.weight() * k);
				InstanceContentEvent instanceContentEvent = new InstanceContentEvent(
						inEvent.getInstanceIndex(), weightedInst, false, true);
				instanceContentEvent.setClassifierIndex(i);
				instanceContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());	
				predictionStream.put(instanceContentEvent);
			}
		}
                
                		/* Estimate model parameters using the training data. */
		if (inEvent.isTraining()) {
			train(inEvent);
		} 
		return false;
	}

	/** The random. */
	protected Random random = new Random();

	/**
	 * Train.
	 *
	 * @param inEvent the in event
	 */
	protected void train(InstanceContentEvent inEvent) {
		Instance trainInst = inEvent.getInstance();
		for (int i = 0; i < sizeEnsemble; i++) {
			int k = MiscUtils.poisson(1.0, this.random);
			if (k > 0) {
				Instance weightedInst = trainInst.copy();
				weightedInst.setWeight(trainInst.weight() * k);
				InstanceContentEvent instanceContentEvent = new InstanceContentEvent(
						inEvent.getInstanceIndex(), weightedInst, true, false);
				instanceContentEvent.setClassifierIndex(i);
				instanceContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());	
				trainingStream.put(instanceContentEvent);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.s4.core.ProcessingElement#onCreate()
	 */
	@Override
	public void onCreate(int id) {
		//do nothing
	}


	/**
	 * Gets the training stream.
	 *
	 * @return the training stream
	 */
	public Stream getTrainingStream() {
		return trainingStream;
	}

	/**
	 * Sets the training stream.
	 *
	 * @param trainingStream the new training stream
	 */
	public void setOutputStream(Stream trainingStream) {
		this.trainingStream = trainingStream;
	}

	/**
	 * Gets the prediction stream.
	 *
	 * @return the prediction stream
	 */
	public Stream getPredictionStream() {
		return predictionStream;
	}

	/**
	 * Sets the prediction stream.
	 *
	 * @param predictionStream the new prediction stream
	 */
	public void setPredictionStream(Stream predictionStream) {
		this.predictionStream = predictionStream;
	}

	/**
	 * Gets the size ensemble.
	 *
	 * @return the size ensemble
	 */
	public int getSizeEnsemble() {
		return sizeEnsemble;
	}

	/**
	 * Sets the size ensemble.
	 *
	 * @param sizeEnsemble the new size ensemble
	 */
	public void setSizeEnsemble(int sizeEnsemble) {
		this.sizeEnsemble = sizeEnsemble;
	}
	
	
	/* (non-Javadoc)
	 * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
	 */
	@Override
	public Processor newProcessor(Processor sourceProcessor) {
		BaggingDistributorProcessor newProcessor = new BaggingDistributorProcessor();
		BaggingDistributorProcessor originProcessor = (BaggingDistributorProcessor) sourceProcessor;
		if (originProcessor.getPredictionStream() != null){
			newProcessor.setPredictionStream(originProcessor.getPredictionStream());
		}
		if (originProcessor.getTrainingStream() != null){
			newProcessor.setOutputStream(originProcessor.getTrainingStream());
		}
		newProcessor.setSizeEnsemble(originProcessor.getSizeEnsemble());
		/*if (originProcessor.getLearningCurve() != null){
			newProcessor.setLearningCurve((LearningCurve) originProcessor.getLearningCurve().copy());
		}*/
		return newProcessor;
	}

}
