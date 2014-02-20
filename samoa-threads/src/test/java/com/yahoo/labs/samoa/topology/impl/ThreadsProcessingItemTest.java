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
import com.yahoo.labs.samoa.utils.EventAllocationType;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsProcessingItemTest {

	@Tested private ThreadsProcessingItem pi;
	
	@Mocked private ThreadsEngine unused;
	@Mocked private ExecutorService threadPool;
	@Mocked private ThreadsEventRunnable task;
	
	@Mocked private Processor processor;
	@Mocked private ThreadsStream stream;
	@Mocked private ContentEvent event;
	
	private final int parallelism = 4;
	private final int counter = 2;
	
	private ThreadsWorkerProcessingItem worker;
	
	
	@Before
	public void setUp() throws Exception {
		pi = new ThreadsProcessingItem(processor, parallelism);
	}

	@Test
	public void testConstructor() {
		assertSame("Processor was not set correctly.",processor,pi.getProcessor());
		assertEquals("Parallelism was not set correctly.",parallelism,pi.getParalellism(),0);
	}
	
	@Test
	public void testConnectInputShuffleStream() {
		new Expectations() {
			{
				stream.addDestination(pi, parallelism, EventAllocationType.SHUFFLE);
			}
		};
		pi.connectInputShuffleStream(stream);
	}
	
	@Test
	public void testConnectInputKeyStream() {
		new Expectations() {
			{
				stream.addDestination(pi, parallelism, EventAllocationType.GROUP_BY_KEY);
			}
		};
		pi.connectInputKeyStream(stream);
	}
	
	@Test
	public void testConnectInputAllStream() {
		new Expectations() {
			{
				stream.addDestination(pi, parallelism, EventAllocationType.BROADCAST);
			}
		};
		pi.connectInputAllStream(stream);
	}
	
	@Test
	public void testSetupWorkers() {
		new Expectations() {
			{
				for (int i=0; i<parallelism; i++) {
					processor.newProcessor(processor);
					result=processor;
				
					processor.onCreate(anyInt);
				}
			}
		};
		pi.setupWorkers();
		List<ThreadsWorkerProcessingItem> workers = pi.getWorkerProcessingItems();
		assertNotNull("List of workers is null.",workers);
		assertEquals("Number of workers does not match parallelism.",parallelism,workers.size(),0);
		for(int i=0; i<workers.size();i++) {
			assertNotNull("Worker "+i+" is null.",workers.get(i));
			assertEquals("Worker "+i+" is not a ThreadsWorkerProcessingItem.",ThreadsWorkerProcessingItem.class,workers.get(i).getClass());
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
		pi.setupWorkers();
		worker = pi.getWorkerProcessingItems().get(counter);
		
		new NonStrictExpectations() {
			{
				ThreadsEngine.getThreadWithIndex(anyInt);
				result=threadPool;
				
				new ThreadsEventRunnable(worker, event);
				result=task;
				
				threadPool.submit(task);
			}
		};
		pi.processEvent(event, counter);
		
	}

}
