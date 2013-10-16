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

import com.yahoo.labs.samoa.moa.core.DoubleVector;
import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.core.ResultContentEvent;
import com.yahoo.labs.samoa.topology.Stream;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


/**
 * The Class PredictionCombinerProcessor.
 */
final public class PredictionCombinerProcessor implements Processor {

	/** The Constant logger. */
//	private static final Logger logger = LoggerFactory
//			.getLogger(PredictionCombinerProcessor.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1606045723451191132L;

	/** The size ensemble. */
	private int sizeEnsemble;
	
	/** The output stream. */
	private Stream outputStream;
	
	/** The number of events received. */
	private int numEventsReceived = 0;
	
	/**
	 * Sets the output stream.
	 *
	 * @param stream the new output stream
	 */
	public void setOutputStream(Stream stream) {
		outputStream = stream;
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
	 * Gets the size ensemble.
	 *
	 * @return the sizeEnsemble
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

	/** The combined vote. */
	private DoubleVector combinedVote;
	

	/**
	 * On event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean process(ContentEvent event) {

		
		ResultContentEvent inEvent = (ResultContentEvent) event; //((s4Event) event).getContentEvent();
		double[] prediction = inEvent.getClassVotes();

		DoubleVector vote = new DoubleVector(prediction);
		if (vote.sumOfValues() > 0.0) {
			vote.normalize();
			combinedVote.addValues(vote);
		}

		//System.out.println("BaggingPredictor"+inEvent.getInstanceIndex()+" "+numEventsReceived+" "+inEvent.getEvaluationIndex() );
		if (inEvent.isLastEvent() || ++numEventsReceived == sizeEnsemble) {
			ResultContentEvent outContentEvent = new ResultContentEvent(inEvent.getInstanceIndex(),
					inEvent.getInstance(), inEvent.getClassId(),
					combinedVote.getArrayCopy(), inEvent.isLastEvent());
			outContentEvent.setEvaluationIndex(inEvent.getEvaluationIndex());
			outputStream.put(outContentEvent);
			//System.out.println("BaggingPredictor close"+inEvent.getInstanceIndex());
			/* This PE instance is no longer needed. */
			//close();
                        //this.reset();
                        numEventsReceived = 0;
                        combinedVote = new DoubleVector();
			return true;
		}
		return false;

	}


	/* (non-Javadoc)
	 * @see samoa.core.Processor#onCreate(int)
	 */
	@Override
	public void onCreate(int id) {
            this.reset();
        }
        
        public void reset(){
		combinedVote = new DoubleVector();
		numEventsReceived = 0;
		//System.out.println("BaggingPredictor create"+id);
	}


	/* (non-Javadoc)
	 * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
	 */
	@Override
	public Processor newProcessor(Processor sourceProcessor) {
		PredictionCombinerProcessor newProcessor = new PredictionCombinerProcessor();
		PredictionCombinerProcessor originProcessor = (PredictionCombinerProcessor) sourceProcessor;
		if (originProcessor.getOutputStream() != null){
			newProcessor.setOutputStream(originProcessor.getOutputStream());
		}
		newProcessor.setSizeEnsemble(originProcessor.getSizeEnsemble());
		return newProcessor;
	}
	
}
