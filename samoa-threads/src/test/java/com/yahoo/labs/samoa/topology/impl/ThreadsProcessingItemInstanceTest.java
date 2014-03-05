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
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsProcessingItemInstanceTest {

	@Tested private ThreadsProcessingItemInstance piInstance;
	
	@Mocked private Processor processor;
	@Mocked private ContentEvent event;
	
	private final int threadIndex = 2;
	
	@Before
	public void setUp() throws Exception {
		piInstance = new ThreadsProcessingItemInstance(processor, threadIndex);
	}

	@Test
	public void testConstructor() {
		assertSame("Processor is not set correctly.", processor, piInstance.getProcessor());
		assertEquals("Thread index is not set correctly.", threadIndex, piInstance.getThreadIndex(),0);
	}
	
	@Test
	public void testProcessEvent() {
		piInstance.processEvent(event);
		new Verifications() {
			{
				processor.process(event); times=1;
			}
		};
	}

}
