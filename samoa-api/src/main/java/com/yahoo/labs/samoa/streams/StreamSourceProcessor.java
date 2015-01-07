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

/**
 * License
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.InstanceContentEvent;
import com.yahoo.labs.samoa.moa.streams.InstanceStream;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * The Class StreamSourceProcessor.
 */
public class StreamSourceProcessor implements Processor {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(StreamSourceProcessor.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -204182279475432739L;

	/** The stream source. */
	private StreamSource streamSource;

	/**
	 * Gets the stream source.
	 *
	 * @return the stream source
	 */
	public StreamSource getStreamSource() {
		return streamSource;
	}

	/**
	 * Sets the stream source.
	 *
	 * @param stream the new stream source
	 */
	public void setStreamSource(InstanceStream stream) {
		this.streamSource = new StreamSource(stream);
		firstInstance = streamSource.nextInstance().getData();
	}

	/** The number instances sent. */
	private long numberInstancesSent = 0;

	/**
	 * Send instances.
	 *  @param inputStream the input stream
	 * @param numberInstances the number instances
	 * @param isTraining the is training
	 */
	public void sendInstances(Stream inputStream,
														int numberInstances, boolean isTraining, boolean isTesting) {
		int numberSamples = 0;

		while (streamSource.hasMoreInstances()
				&& numberSamples < numberInstances) {
			
			numberSamples++;
			numberInstancesSent++;
			InstanceContentEvent instanceContentEvent = new InstanceContentEvent(
					numberInstancesSent, nextInstance(), isTraining, isTesting);
		
			
			inputStream.put(instanceContentEvent);
		}

		InstanceContentEvent instanceContentEvent = new InstanceContentEvent(
				numberInstancesSent, null, isTraining, isTesting);
		instanceContentEvent.setLast(true);
		inputStream.put(instanceContentEvent);
	}

	/**
	 * Send end evaluation instance.
	 *
	 * @param inputStream the input stream
	 */
	public void sendEndEvaluationInstance(Stream inputStream) {
		InstanceContentEvent instanceContentEvent = new InstanceContentEvent(-1, firstInstance,false, true);
		inputStream.put(instanceContentEvent);
	}

	/**
	 * Next instance.
	 *
	 * @return the instance
	 */
	protected Instance nextInstance() {
		if (this.isInited) {
			return streamSource.nextInstance().getData();
		} else {
			this.isInited = true;
			return firstInstance;
		}
	}

	/** The is inited. */
	protected boolean isInited = false;
	
	/** The first instance. */
	protected Instance firstInstance;

	//@Override
	/**
	 * On remove.
	 */
	protected void onRemove() {
	}

	/* (non-Javadoc)
	 * @see samoa.core.Processor#onCreate(int)
	 */
	@Override
	public void onCreate(int id) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
	 */
	@Override
	public Processor newProcessor(Processor sourceProcessor) {
//		StreamSourceProcessor newProcessor = new StreamSourceProcessor();
//		StreamSourceProcessor originProcessor = (StreamSourceProcessor) sourceProcessor;
//		if (originProcessor.getStreamSource() != null){
//			newProcessor.setStreamSource(originProcessor.getStreamSource().getStream());
//		}
		//return newProcessor;
		return null;
	}

	/**
	 * On event.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	@Override
	public boolean process(ContentEvent event) {
		return false;
	}
	
	
	/**
	 * Gets the dataset.
	 *
	 * @return the dataset
	 */
	public Instances getDataset() {
		return firstInstance.dataset();
	}

}
