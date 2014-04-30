package com.yahoo.labs.samoa.topology.impl;

import static org.junit.Assert.*;

import java.util.Set;

import mockit.NonStrictExpectations;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;
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



import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;

/**
 * @author Anh Thu Vu
 *
 */
public class SimpleTopologyTest {

	@Tested private SimpleTopology topology;
	
	@Mocked private SimpleEntranceProcessingItem entrancePi;
	@Mocked private EntranceProcessor entranceProcessor;
	
	@Before
	public void setUp() throws Exception {
		topology = new SimpleTopology("TestTopology");
	}

	@Test
	public void testAddEntrancePi() {
		topology.addEntranceProcessingItem(entrancePi);
		
		Set<EntranceProcessingItem> entrancePIs = topology.getEntranceProcessingItems();
		assertNotNull("Set of entrance PIs is null.",entrancePIs);
		assertEquals("Number of entrance PI in SimpleTopology must be 1",1,entrancePIs.size());
		assertSame("Entrance PI was not set correctly.",entrancePi,entrancePIs.toArray()[0]);
		// TODO: verify that entrance PI is in the set of ProcessingItems
		// Need to access topology's set of PIs (getProcessingItems() method)
	}
	
	@Test
	public void testRun() {
		topology.addEntranceProcessingItem(entrancePi);
		
		new NonStrictExpectations() {
			{
				entrancePi.getProcessor();
				result=entranceProcessor;
				
			}
		};
		
		new Expectations() {
			{
				entranceProcessor.onCreate(anyInt);	
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
