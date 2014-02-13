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

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.ProcessingItem;

/**
 * @author Anh Thu Vu
 *
 */
@RunWith(Parameterized.class)
public class ThreadsStreamTest {
	
	@Tested private ThreadsStream stream;
	
	@Mocked private ThreadsProcessingItem sourcePi, destPi;
	@Mocked private ContentEvent event;

	private final int parallelism;
	private final EventAllocationType type;
	
	@Parameters
	public static Collection<Object[]> generateParameters() {
		return Arrays.asList(new Object[][] {
			 { 2, EventAllocationType.SHUFFLE },
			 { 3, EventAllocationType.GROUP_BY_KEY },
			 { 4, EventAllocationType.BROADCAST }
		});
	}
	
	public ThreadsStreamTest(int parallelism, EventAllocationType type) {
		this.parallelism = parallelism;
		this.type = type;
	}
	
	@Before
	public void setUp() throws Exception {
		stream = new ThreadsStream(sourcePi);
		stream.addDestination(destPi, parallelism, type);
	}

	@Test
	public void testPut() {
		switch(type) {
		case SHUFFLE: case GROUP_BY_KEY:
			new Expectations() {
				{
					// TODO: restrict the range of counter value
					destPi.processEvent(event, anyInt); times=1;
				}
			};
			break;
		case BROADCAST:
			new Expectations() {
				{
					// TODO: restrict the range of counter value
					destPi.processEvent(event, anyInt); times=parallelism;
				}
			};
			break;
		}
		stream.put(event);
	}
	
	@Test
	public void testAddDestination() {
		Set<DestinationPIWrapper> destinations = stream.getDestinations();
		boolean found = false;
		for (DestinationPIWrapper destination:destinations) {
			if (destination.getProcessingItem() == destPi &&
					destination.getParallelism() == parallelism &&
					destination.getEventAllocationType() == type) {
				found = true;
				break;
			}
		}
		
		assertTrue("Destination PI was not added in stream's destinations set.",found);
	}

}
