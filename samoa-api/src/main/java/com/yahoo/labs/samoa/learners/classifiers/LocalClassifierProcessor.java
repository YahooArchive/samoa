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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.learners.ResultContentEvent;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.moa.classifiers.core.driftdetection.ChangeDetector;
import static com.yahoo.labs.samoa.moa.core.Utils.maxIndex;
import com.yahoo.labs.samoa.topology.Stream;
//import weka.core.Instance;

/**
 * The Class LearnerProcessor.
 */
final public class LocalClassifierProcessor implements Processor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1577910988699148691L;

	private static final Logger logger = LoggerFactory
			.getLogger(LocalClassifierProcessor.class);
	
	private LocalClassifierAdapter model;
	private Stream outputStream;
	private int modelId;
	private long instancesCount = 0;

	/**
	 * Sets the learner.
	 *
	 * @param model the model to set
	 */
	public void setClassifier(LocalClassifierAdapter model) {
		this.model = model;
	}

	/**
	 * Gets the learner.
	 *
	 * @return the model
	 */
	public LocalClassifierAdapter getLearner() {
		return model;
	}

	/**
	 * Set the output streams.
	 *
	 * @param outputStream the new output stream
	 * {@link PredictionCombinerPE}.
	 */
	public void setOutputStream(Stream outputStream) {

		this.outputStream = outputStream;
	}
	
	/**
	 * Gets the output stream.
	 *
	 * @return the output stream
	 */
	public Stream getOutputStream() {
		return outputStream;
	}

	/**
	 * Gets the instances count.
	 *
	 * @return number of observation vectors used in training iteration.
	 */
	public long getInstancesCount() {
		return instancesCount;
	}

	/**
	 * Update stats.
	 *
	 * @param event the event
	 */
	private void updateStats(InstanceContentEvent event) {
                Instance inst = event.getInstance();
		this.model.trainOnInstance(inst);
		this.instancesCount++;
		if (instancesCount % 10000 == 0) {
			logger.info("Trained model using {} events with classifier id {}",
					instancesCount, this.modelId); //getId());
		}
                if (this.changeDetector != null) {
                    boolean correctlyClassifies = this.correctlyClassifies(inst);
                    double oldEstimation = this.changeDetector.getEstimation();
                    this.changeDetector.input(correctlyClassifies ? 0 : 1);
                    if (this.changeDetector.getEstimation() > oldEstimation) {
                        //Start a new classifier
                        this.model.resetLearning();
                        this.changeDetector.resetLearning();
                    }
                }
	}

	private boolean correctlyClassifies(Instance inst) {
            return maxIndex(model.getVotesForInstance(inst)) == (int) inst.classValue();
        }
        
	/** The test. */
	protected int test; //to delete
	
	/**
	 * On event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
    @Override
	public boolean process(ContentEvent event) {

    	InstanceContentEvent inEvent = (InstanceContentEvent) event;
		Instance instance = inEvent.getInstance();

		if (inEvent.getInstanceIndex() < 0) {
			//end learning
			ResultContentEvent outContentEvent = new ResultContentEvent(-1, instance, 0,
					new double[0], inEvent.isLastEvent());
			outContentEvent.setClassifierIndex(this.modelId);
			outContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());
			outputStream.put(outContentEvent);
			return false;
		}
		
		if (inEvent.isTesting()){
			double[] dist = model.getVotesForInstance(instance);
			ResultContentEvent outContentEvent = new ResultContentEvent(inEvent.getInstanceIndex(),
					instance, inEvent.getClassId(), dist, inEvent.isLastEvent());
			outContentEvent.setClassifierIndex(this.modelId);
			outContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());
			logger.trace(inEvent.getInstanceIndex() + " " // +
															// inEvent.getClassId()
					+ " " + modelId + " " + dist);
			outputStream.put(outContentEvent);
			
			if (++test % 10000 == 0) {
				logger.info("Tested model using {} events with classifier id {}",
						test, this.modelId);
			}
		}
		
		if (inEvent.isTraining()) {
			updateStats(inEvent);
		} 
		return false;
	}

	/* (non-Javadoc)
	 * @see samoa.core.Processor#onCreate(int)
	 */
	@Override
	public void onCreate(int id) {
		this.modelId = id;
		model = model.create();
	}

	/* (non-Javadoc)
	 * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
	 */
	@Override
	public Processor newProcessor(Processor sourceProcessor) {
		LocalClassifierProcessor newProcessor = new LocalClassifierProcessor();
		LocalClassifierProcessor originProcessor = (LocalClassifierProcessor) sourceProcessor;
		if (originProcessor.getLearner() != null){
			newProcessor.setClassifier(originProcessor.getLearner().create());
		}
		newProcessor.setOutputStream(originProcessor.getOutputStream());
		return newProcessor;
	}
        
        protected ChangeDetector changeDetector;    
        
        public ChangeDetector getChangeDetector() {
            return this.changeDetector;
        }

        public void setChangeDetector(ChangeDetector cd) {
            this.changeDetector = cd;
        }
        

}
