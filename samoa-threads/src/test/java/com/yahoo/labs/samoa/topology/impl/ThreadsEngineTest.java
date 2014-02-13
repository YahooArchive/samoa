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
import org.junit.Before;
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
	
	private final int delay = 10;

	@After
	public void cleanup() {
		ThreadsEngine.shutdown();
	}
	
	@Test
	public void testSetNumThreadsSimple() {
		ThreadsEngine.setNumberThreads(numThreads);
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberThreads(),0);
	}
	
	@Test
	public void testSetNumThreadsRepeat() {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.setNumberThreads(numThreads);
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberThreads(),0);
	}
	
	@Test
	public void testSetNumThreadsIncrease() {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.setNumberThreads(numThreadsLarger);
		assertEquals("Number of threads is not set correctly.", numThreadsLarger,
				ThreadsEngine.getNumberThreads(),0);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetNumThreadsDecrease() {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.setNumberThreads(numThreadsSmaller);
		// Exception expected
	}
	
	@Test
	public void testShutDown() {
		ThreadsEngine.setNumberThreads(numThreads);
		ThreadsEngine.shutdown();
		assertEquals("ThreadsEngine was not shutdown properly.", 0, ThreadsEngine.getNumberThreads());
	}

	@Test
	public void testGetThreadWithIndex() {
		ThreadsEngine.setNumberThreads(numThreads);
		for (int i=0; i<numThreads; i++) {
			assertNotNull("ExecutorService is not initialized correctly.", ThreadsEngine.getThreadWithIndex(i));
		}
	}

	@Test
	public void testSubmitTopology() {
		ThreadsEngine.submitTopology(topology, delay, numThreads);
		new Verifications() {{
		    topology.start(delay); times=1;
		}};
		assertEquals("Number of threads is not set correctly.", numThreads,
				ThreadsEngine.getNumberThreads(),0);
	}

}
