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
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsTopologyTest {

	@Tested private ThreadsTopology topology;
	
	@Mocked private ThreadsEntranceProcessingItem entrancePi;
	@Mocked private Thread unused;
	
	@Before
	public void setUp() throws Exception {
		topology = new ThreadsTopology("testTopology");
	}

	@Test
	public void testAddEntrancePi() {
		topology.addEntrancePi(entrancePi);
		assertSame("Entrance PI was not set correctly.",entrancePi,topology.getEntranceProcessingItem());
		// TODO: verify that entrance PI is in the set of ProcessingItems
		// Need to access topology's set of PIs (getProcessingItems() method)
	}
	
	@Test
	public void testRun() {
		topology.addEntrancePi(entrancePi);
		new Expectations() {
			{
				entrancePi.startSendingEvents();
			}
		};
		topology.run();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRunWithoutEntrancePI() {
		topology.run();
	}

}
