package com.yahoo.labs.samoa.learners.classifiers.trees;

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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.learners.ResultContentEvent;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.learners.InstancesContentEvent;
import com.yahoo.labs.samoa.topology.Stream;
import java.util.LinkedList;
import java.util.List;

/**
 * Filter Processor that stores and filters the instances before 
 * sending them to the Model Aggregator Processor.

 * @author Arinto Murdopo
 *
 */
final class FilterProcessor implements Processor {

	private static final long serialVersionUID = -1685875718300564885L;
	private static final Logger logger = LoggerFactory.getLogger(FilterProcessor.class);

	private int processorId;
	
	private final Instances dataset;
	private InstancesHeader modelContext;
	
	//available streams
	private Stream outputStream;
		
	//private constructor based on Builder pattern
	private FilterProcessor(Builder builder){
		this.dataset = builder.dataset;
                this.batchSize = builder.batchSize;
                this.delay = builder.delay;
	}	
                
        private int waitingInstances = 0;
        
        private int delay = 0;
        
        private int batchSize = 200;
                
        private List<InstanceContentEvent> contentEventList = new LinkedList<InstanceContentEvent>();
        
	@Override
	public boolean process(ContentEvent event) {    
            //Receive a new instance from source
            if(event instanceof InstanceContentEvent){
                InstanceContentEvent instanceContentEvent = (InstanceContentEvent) event;
                this.contentEventList.add(instanceContentEvent);
                this.waitingInstances++;
                if (this.waitingInstances == this.batchSize || instanceContentEvent.isLastEvent()){
                    //Send Instances
                    InstancesContentEvent outputEvent = new InstancesContentEvent(instanceContentEvent);
                    boolean isLastEvent = false;
                    while (!this.contentEventList.isEmpty()){
                        InstanceContentEvent ice = this.contentEventList.remove(0);
                        Instance inst = ice.getInstance();
                        outputEvent.add(inst);
                        if (!isLastEvent) {
                            isLastEvent = ice.isLastEvent();
                        }
                    }
                    outputEvent.setLast(isLastEvent);
                    this.waitingInstances = 0;
                    this.outputStream.put(outputEvent);
                    if (this.delay > 0) {
                        try {
                            Thread.sleep(this.delay);
                        } catch(InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }       
                }
            } 
            return false;
        }
            
	@Override
	public void onCreate(int id) {
		this.processorId = id;
                this.waitingInstances = 0;
		
        }

	@Override
	public Processor newProcessor(Processor p) {
		FilterProcessor oldProcessor = (FilterProcessor)p;
		FilterProcessor newProcessor = 
				new FilterProcessor.Builder(oldProcessor).build();
		
		newProcessor.setOutputStream(oldProcessor.outputStream);
		return newProcessor;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		return sb.toString();
	}
	
	void setOutputStream(Stream outputStream){
		this.outputStream = outputStream;
	}
	
	
	/**
	 * Helper method to generate new ResultContentEvent based on an instance and
	 * its prediction result.
	 * @param prediction The predicted class label from the decision tree model.
	 * @param inEvent The associated instance content event
	 * @return ResultContentEvent to be sent into Evaluator PI or other destination PI.
	 */
	private ResultContentEvent newResultContentEvent(double[] prediction, InstanceContentEvent inEvent){
		ResultContentEvent rce = new ResultContentEvent(inEvent.getInstanceIndex(), inEvent.getInstance(), inEvent.getClassId(), prediction, inEvent.isLastEvent());
		rce.setClassifierIndex(this.processorId);
		rce.setEvaluationIndex(inEvent.getEvaluationIndex());
		return rce;
	}
			
       
	/**
	 * Builder class to replace constructors with many parameters
	 * @author Arinto Murdopo
	 *
	 */
	static class Builder{
		
		//required parameters
		private final Instances dataset;
		
		private int delay = 0;
        
    private int batchSize = 200;

		Builder(Instances dataset){
			this.dataset = dataset;
		}
		
		Builder(FilterProcessor oldProcessor){
			this.dataset = oldProcessor.dataset;
			this.delay = oldProcessor.delay;
			this.batchSize = oldProcessor.batchSize;
		}
                
		public Builder delay(int delay){
			this.delay = delay;
			return this;
		}
                
		public Builder batchSize(int val){
			this.batchSize = val;
			return this;
		}
              	
		FilterProcessor build(){
			return new FilterProcessor(this);
		}
	}
	
}
