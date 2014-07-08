package com.yahoo.labs.samoa.topology.impl;

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

import static org.junit.Assert.*;
import mockit.Mocked;
import mockit.Verifications;

import org.junit.After;
import org.junit.Test;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsEngineTest {

	@Mocked ThreadsTopology topology;
	
	private final int numThreads = 4;
	private final int numThreadsSmaller = 3;
	private final int numThreadsLarger = 5;

	@After
	public void cleanup() {
		ThreadsEngine.clearThreadPool();
	}
	
	@Test
	public void testSetNumberOfThreadsSimple() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberOfThreads(),0);
	}
	
	@Test
	public void testSetNumberOfThreadsRepeat() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		ThreadsEngine.setNumberOfThreads(numThreads);
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberOfThreads(),0);
	}
	
	@Test
	public void testSetNumberOfThreadsIncrease() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		ThreadsEngine.setNumberOfThreads(numThreadsLarger);
		assertEquals("Number of threads is not set correctly.", numThreadsLarger,
				ThreadsEngine.getNumberOfThreads(),0);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetNumberOfThreadsDecrease() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		ThreadsEngine.setNumberOfThreads(numThreadsSmaller);
		// Exception expected
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetNumberOfThreadsNegative() {
		ThreadsEngine.setNumberOfThreads(-1);
		// Exception expected
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetNumberOfThreadsZero() {
		ThreadsEngine.setNumberOfThreads(0);
		// Exception expected
	}
	
	@Test
	public void testClearThreadPool() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		ThreadsEngine.clearThreadPool();
		assertEquals("ThreadsEngine was not shutdown properly.", 0, ThreadsEngine.getNumberOfThreads());
	}

	@Test
	public void testGetThreadWithIndexWithinPoolSize() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		for (int i=0; i<numThreads; i++) {
			assertNotNull("ExecutorService is not initialized correctly.", ThreadsEngine.getThreadWithIndex(i));
		}
	}
	
	@Test
	public void testGetThreadWithIndexOutOfPoolSize() {
		ThreadsEngine.setNumberOfThreads(numThreads);
		for (int i=0; i<numThreads+3; i++) {
			assertNotNull("ExecutorService is not initialized correctly.", ThreadsEngine.getThreadWithIndex(i));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void testGetThreadWithIndexFromEmptyPool() {
		for (int i=0; i<numThreads; i++) {
			ThreadsEngine.getThreadWithIndex(i);
		}
	}

	@Test
	public void testSubmitTopology() {
		ThreadsEngine.submitTopology(topology, numThreads);
		new Verifications() {{
		    topology.run(); times=1;
		}};
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberOfThreads(),0);
	}

}
