package com.yahoo.labs.samoa.utils;

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

import java.util.Arrays;
import java.util.Collection;

import mockit.Mocked;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.utils.PartitioningScheme;
import com.yahoo.labs.samoa.utils.StreamDestination;

/**
 * @author Anh Thu Vu
 *
 */
@RunWith(Parameterized.class)
public class StreamDestinationTest {

	@Tested private StreamDestination destination;
	
	@Mocked private IProcessingItem pi;
	private final int parallelism;
	private final PartitioningScheme scheme;
	
	@Parameters
	public static Collection<Object[]> generateParameters() {
		return Arrays.asList(new Object[][] {
			 { 3, PartitioningScheme.SHUFFLE },
			 { 2, PartitioningScheme.GROUP_BY_KEY },
			 { 5, PartitioningScheme.BROADCAST }
		});
	}
	
	public StreamDestinationTest(int parallelism, PartitioningScheme scheme) {
		this.parallelism = parallelism;
		this.scheme = scheme;
	}
	
	@Before
	public void setUp() throws Exception {
		destination = new StreamDestination(pi, parallelism, scheme);
	}

	@Test
	public void testContructor() {
		assertSame("The IProcessingItem is not set correctly.", pi, destination.getProcessingItem());
		assertEquals("Parallelism value is not set correctly.", parallelism, destination.getParallelism(), 0);
		assertEquals("EventAllocationType is not set correctly.", scheme, destination.getPartitioningScheme());
	}

}
