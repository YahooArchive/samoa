package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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

import static org.junit.Assert.*;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.utils.PriorityThreadPoolExecutor;

public class ParallelMasterProcessingItemTest {
    
	private int parallelismHint = 3;
	private int testIndex = 1;
	
	@Tested private ParallelMasterProcessingItem pi;
	@Mocked private Processor orgProcessor;
	@Mocked private Processor childProcessor;
	@Mocked private ParallelStream stream;
	@Mocked private ParallelEngine unusedEngine; 
	@Mocked private ContentEvent event;
	@Mocked private ParallelProcessingTask task;
	
	private final PriorityThreadPoolExecutor executor = new PriorityThreadPoolExecutor();
	private IProcessingItem worker;

	@Before
	public void setUp() throws Exception {
		pi = new ParallelMasterProcessingItem(orgProcessor, this.parallelismHint);
	}

	@Test
	public void testConstructor() {
		assertSame("Processor is not initalized correctly",orgProcessor,pi.getProcessor());
		assertEquals("Parallelism hint is not initialized correctly",parallelismHint,pi.getParalellism(),0);
	}
	
	@Test
	public void testCreateWorkerProcessingItem() {
		new NonStrictExpectations() {
			{
				// Verify the newly created attached Processors
				orgProcessor.newProcessor(orgProcessor); 
				result=childProcessor; times=parallelismHint;
				
				childProcessor.onCreate(anyInt); times=parallelismHint;
			}
		};
		
		pi.createWorkerProcessingItem();
		
		// Verify that WorkerProcessingItem(s) are created
		for (int i=0; i<parallelismHint; i++) {
			assertNotNull("Worker PIs are not initialized",pi.getWorkerProcessingItem(i));
		}
	}
	
	@Test
	public void testConnectInputShuffleStream() {
		pi.connectInputShuffleStream(stream);
		new Verifications() {
			{
				stream.add(pi, ParallelProcessingItem.SHUFFLE, parallelismHint); times=1;
			}
		};
	}
	
	@Test
	public void testConnectInputKeyStream() {
		pi.connectInputKeyStream(stream);
		new Verifications() {
			{
				stream.add(pi, ParallelProcessingItem.GROUP_BY_KEY, parallelismHint); times=1;
			}
		};
	}
	
	@Test
	public void testConnectInputAllStream() {
		pi.connectInputAllStream(stream);
		new Verifications() {
			{
				stream.add(pi, ParallelProcessingItem.BROADCAST, parallelismHint); times=1;
			}
		};
	}
	
	@Test
	public void testProcessEvent() {
		worker = pi.getWorkerProcessingItem(testIndex);
		
		new NonStrictExpectations() {
			{
				new ParallelProcessingTask(worker, event, anyInt, anyLong);
				result = task;
				
				ParallelEngine.getExecutorService(anyInt);
				result = executor;
				
				executor.submit(task); times=1;
			}
		};
		
		pi.processEvent(event, testIndex);
	}

}
