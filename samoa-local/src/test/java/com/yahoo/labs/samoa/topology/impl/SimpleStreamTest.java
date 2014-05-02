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
public class SimpleStreamTest {

	@Tested private SimpleStream stream;
	
	@Mocked private SimpleProcessingItem sourcePi, destPi;
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
	
	public SimpleStreamTest(int parallelism, PartitioningScheme scheme) {
		this.parallelism = parallelism;
		this.scheme = scheme;
	}
	
	@Before
	public void setUp() throws Exception {
		stream = new SimpleStream(sourcePi);
		stream.addDestination(destination);
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
