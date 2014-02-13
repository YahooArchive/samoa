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

/**
 * @author Anh Thu Vu
 *
 */
@RunWith(Parameterized.class)
public class DestinationPIWrapperTest {

	@Tested private DestinationPIWrapper wrapper;
	
	@Mocked private IProcessingItem pi;
	private final int parallelism;
	private final EventAllocationType type;
	
	@Parameters
	public static Collection<Object[]> generateParameters() {
		return Arrays.asList(new Object[][] {
			 { 3, EventAllocationType.SHUFFLE },
			 { 2, EventAllocationType.GROUP_BY_KEY },
			 { 5, EventAllocationType.BROADCAST }
		});
	}
	
	public DestinationPIWrapperTest(int parallelism, EventAllocationType type) {
		this.parallelism = parallelism;
		this.type = type;
	}
	
	@Before
	public void setUp() throws Exception {
		wrapper = new DestinationPIWrapper(pi, parallelism, type);
	}

	@Test
	public void testContructor() {
		assertSame("The IProcessingItem is not set correctly.", pi, wrapper.getProcessingItem());
		assertEquals("Parallelism value is not set correctly.", parallelism, wrapper.getParallelism(), 0);
		assertEquals("EventAllocationType is not set correctly.", type, wrapper.getEventAllocationType());
	}

}
