package com.yahoo.labs.samoa.streams;

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

import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * Prequential Source Processor is the processor for Prequential Evaluation
 * Task.
 * @author Arinto Murdopo
 *
 */
public final class PrequentialSourceProcessor implements EntranceProcessor {

	private static final long serialVersionUID = 4169053337917578558L;

	private static final Logger logger = 
			LoggerFactory.getLogger(PrequentialSourceProcessor.class);
	
	private StreamSource streamSource;
	private Instance firstInstance;
	private boolean isInited = false;
	private int id;
	
	@Override
	public boolean process(ContentEvent event) {
		//TODO: possible refactor of the super-interface implementation
		//of source processor does not need this method
		return false;
	}
	
    @Override
    public ContentEvent nextEvent() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void onCreate(int id) {
		this.id = id;
		logger.debug("Creating PrequentialSourceProcessor with id {}", this.id);
	}

	@Override
	public Processor newProcessor(Processor p) {
		PrequentialSourceProcessor newProcessor = new PrequentialSourceProcessor();
		PrequentialSourceProcessor originProcessor = (PrequentialSourceProcessor) p;
		if(originProcessor.getStreamSource() != null){
			newProcessor.setStreamSource(originProcessor.getStreamSource().getStream());
		}
		return newProcessor;
	}
	
	/**
	 * Method to send instances via input stream
	 * @param inputStream
	 * @param numberInstances
	 */
	public void sendInstances(Stream inputStream, int numberInstances){
		int numInstanceSent = 0;
		initStreamSource(sourceStream);
                
		while(streamSource.hasMoreInstances() && numInstanceSent < numberInstances){
			numInstanceSent++;
			InstanceContentEvent contentEvent = 
					new InstanceContentEvent(numInstanceSent, nextInstance(), true, true);
			inputStream.put(contentEvent);
		}
		
		sendEndEvaluationInstance(inputStream);
	}

	public StreamSource getStreamSource(){
		return streamSource;
	}
	
        protected InstanceStream sourceStream;
        
	public void setStreamSource(InstanceStream stream){
		this.sourceStream = stream;
	}
	
	public Instances getDataset(){
            if (firstInstance == null){
                initStreamSource(sourceStream);
            }
		return firstInstance.dataset();
	}
	
	private Instance nextInstance(){
		if(this.isInited == true){
			return streamSource.nextInstance().getData();
		}else{
			this.isInited = true;
			return firstInstance;
		}
	}
	
	private void sendEndEvaluationInstance(Stream inputStream){
		InstanceContentEvent contentEvent = 
				new InstanceContentEvent(-1, firstInstance, false, true);
		contentEvent.setLast(true);
		inputStream.put(contentEvent);
	}

    private void initStreamSource(InstanceStream stream) {
        if(stream instanceof AbstractOptionHandler){
                ((AbstractOptionHandler)(stream)).prepareForUse();
        }
        
        this.streamSource = new StreamSource(stream);
        firstInstance = streamSource.nextInstance().getData();
    }
}
