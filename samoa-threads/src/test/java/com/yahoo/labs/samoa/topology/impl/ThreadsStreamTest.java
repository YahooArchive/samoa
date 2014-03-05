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

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.utils.PartitioningScheme;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * @author Anh Thu Vu
 *
 */
@RunWith(Parameterized.class)
public class ThreadsStreamTest {
	
	@Tested private ThreadsStream stream;
	
	@Mocked private ThreadsProcessingItem sourcePi, destPi;
	@Mocked private ContentEvent event;
	@Mocked private StreamDestination destination;

	private final String eventKey = "eventkey";
	private final int parallelism;
	private final PartitioningScheme scheme;
	
	
	@Parameters
	public static Collection<Object[]> generateParameters() {
		return Arrays.asList(new Object[][] {
			 { 2, PartitioningScheme.SHUFFLE },
			 { 3, PartitioningScheme.GROUP_BY_KEY },
			 { 4, PartitioningScheme.BROADCAST }
		});
	}
	
	public ThreadsStreamTest(int parallelism, PartitioningScheme scheme) {
		this.parallelism = parallelism;
		this.scheme = scheme;
	}
	
	@Before
	public void setUp() throws Exception {
		stream = new ThreadsStream(sourcePi);
		stream.addDestination(destination);
	}
	
	@Test
	public void testAddDestination() {
		boolean found = false;
		for (StreamDestination sd:stream.getDestinations()) {
			if (sd == destination) {
				found = true;
				break;
			}
		}
		assertTrue("Destination object was not added in stream's destinations set.",found);
	}

	@Test
	public void testPut() {
		new NonStrictExpectations() {
			{
				event.getKey(); result=eventKey;
				destination.getProcessingItem(); result=destPi;
				destination.getPartitioningScheme(); result=scheme;
				destination.getParallelism(); result=parallelism;
				
			}
		};
		switch(this.scheme) {
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
	
	

}
