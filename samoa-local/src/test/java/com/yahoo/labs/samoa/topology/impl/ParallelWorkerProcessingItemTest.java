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
import mockit.Tested;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;

public class ParallelWorkerProcessingItemTest {

	private static final int WORKER_PI_PARALLELISM = 0;
	
	@Tested private ParallelWorkerProcessingItem pi;
	@Mocked private Processor processor;
	@Mocked private ContentEvent event;
	
	@Before
	public void setUp() throws Exception {
		pi = new ParallelWorkerProcessingItem(processor);
	}
	
	@Test
	public void testConstructor() {
		assertSame("Processor does not match expectation", processor, pi.getProcessor());
		assertEquals("Invalid parallelism for worker PI", WORKER_PI_PARALLELISM, pi.getParalellism(), 0);
	}
	
	@Test
	public void testProcessEvent() {
		pi.processEvent(event);
		
		new Verifications() {
			{
				processor.process(event); times = 1;
			}
		};
	}

}
