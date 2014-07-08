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
package com.yahoo.labs.samoa.topology.impl;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ExecutorService;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.utils.PartitioningScheme;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsProcessingItemTest {

	@Tested private ThreadsProcessingItem pi;
	
	@Mocked private ThreadsEngine unused;
	@Mocked private ExecutorService threadPool;
	@Mocked private ThreadsEventRunnable task;
	
	@Mocked private Processor processor, processorReplica;
	@Mocked private ThreadsStream stream;
	@Mocked private StreamDestination destination;
	@Mocked private ContentEvent event;
	
	private final int parallelism = 4;
	private final int counter = 2;
	
	private ThreadsProcessingItemInstance instance;
	
	
	@Before
	public void setUp() throws Exception {
		new NonStrictExpectations() {
			{
				processor.newProcessor(processor);
				result=processorReplica;
			}
		};
		pi = new ThreadsProcessingItem(processor, parallelism);
	}

	@Test
	public void testConstructor() {
		assertSame("Processor was not set correctly.",processor,pi.getProcessor());
		assertEquals("Parallelism was not set correctly.",parallelism,pi.getParallelism(),0);
	}
	
	@Test
	public void testConnectInputShuffleStream() {
		new Expectations() {
			{
				destination = new StreamDestination(pi, parallelism, PartitioningScheme.SHUFFLE);
				stream.addDestination(destination);
			}
		};
		pi.connectInputShuffleStream(stream);
	}
	
	@Test
	public void testConnectInputKeyStream() {
		new Expectations() {
			{
				destination = new StreamDestination(pi, parallelism, PartitioningScheme.GROUP_BY_KEY);
				stream.addDestination(destination);
			}
		};
		pi.connectInputKeyStream(stream);
	}
	
	@Test
	public void testConnectInputAllStream() {
		new Expectations() {
			{
				destination = new StreamDestination(pi, parallelism, PartitioningScheme.BROADCAST);
				stream.addDestination(destination);
			}
		};
		pi.connectInputAllStream(stream);
	}
	
	@Test
	public void testSetupInstances() {
		new Expectations() {
			{
				for (int i=0; i<parallelism; i++) {
					processor.newProcessor(processor);
					result=processor;
				
					processor.onCreate(anyInt);
				}
			}
		};
		pi.setupInstances();
		List<ThreadsProcessingItemInstance> instances = pi.getProcessingItemInstances();
		assertNotNull("List of PI instances is null.",instances);
		assertEquals("Number of instances does not match parallelism.",parallelism,instances.size(),0);
		for(int i=0; i<instances.size();i++) {
			assertNotNull("Instance "+i+" is null.",instances.get(i));
			assertEquals("Instance "+i+" is not a ThreadsWorkerProcessingItem.",ThreadsProcessingItemInstance.class,instances.get(i).getClass());
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void testProcessEventError() {
		pi.processEvent(event, counter);
	}
	
	@Test
	public void testProcessEvent() {
		new Expectations() {
			{
				for (int i=0; i<parallelism; i++) {
					processor.newProcessor(processor);
					result=processor;
				
					processor.onCreate(anyInt);
				}
			}
		};
		pi.setupInstances();
		
		instance = pi.getProcessingItemInstances().get(counter);
		new NonStrictExpectations() {
			{
				ThreadsEngine.getThreadWithIndex(anyInt);
				result=threadPool;
				
				
			}
		};
		new Expectations() {
			{
				task = new ThreadsEventRunnable(instance, event);
				threadPool.submit(task);
			}
		};
		pi.processEvent(event, counter);
		
	}

}
