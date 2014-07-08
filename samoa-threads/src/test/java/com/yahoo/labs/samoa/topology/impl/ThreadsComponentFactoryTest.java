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
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.Topology;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsComponentFactoryTest {
	@Tested private ThreadsComponentFactory factory;
	@Mocked private Processor processor, processorReplica;
	@Mocked private EntranceProcessor entranceProcessor;
	
	private final int parallelism = 3;
	private final String topoName = "TestTopology";
	

	@Before
	public void setUp() throws Exception {
		factory = new ThreadsComponentFactory();
	}

	@Test
	public void testCreatePiNoParallelism() {
		new NonStrictExpectations() {
			{
				processor.newProcessor(processor);
				result=processorReplica;
			}
		};
		ProcessingItem pi = factory.createPi(processor);
		assertNotNull("ProcessingItem created is null.",pi);
		assertEquals("ProcessingItem created is not a ThreadsProcessingItem.",ThreadsProcessingItem.class,pi.getClass());
		assertEquals("Parallelism of PI is not 1",1,pi.getParallelism(),0);
	}
	
	@Test
	public void testCreatePiWithParallelism() {
		new NonStrictExpectations() {
			{
				processor.newProcessor(processor);
				result=processorReplica;
			}
		};
		ProcessingItem pi = factory.createPi(processor,parallelism);
		assertNotNull("ProcessingItem created is null.",pi);
		assertEquals("ProcessingItem created is not a ThreadsProcessingItem.",ThreadsProcessingItem.class,pi.getClass());
		assertEquals("Parallelism of PI is not ",parallelism,pi.getParallelism(),0);
	}
	
	@Test
	public void testCreateStream() {
		new NonStrictExpectations() {
			{
				processor.newProcessor(processor);
				result=processorReplica;
			}
		};
		ProcessingItem pi = factory.createPi(processor);
		
		Stream stream = factory.createStream(pi);
		assertNotNull("Stream created is null",stream);
		assertEquals("Stream created is not a ThreadsStream.",ThreadsStream.class,stream.getClass());
	}
	
	@Test
	public void testCreateTopology() {
		Topology topology = factory.createTopology(topoName);
		assertNotNull("Topology created is null.",topology);
		assertEquals("Topology created is not a ThreadsTopology.",ThreadsTopology.class,topology.getClass());
	}
	
	@Test
	public void testCreateEntrancePi() {
		EntranceProcessingItem entrancePi = factory.createEntrancePi(entranceProcessor);
		assertNotNull("EntranceProcessingItem created is null.",entrancePi);
		assertEquals("EntranceProcessingItem created is not a ThreadsEntranceProcessingItem.",ThreadsEntranceProcessingItem.class,entrancePi.getClass());
		assertSame("EntranceProcessor is not set correctly.",entranceProcessor, entrancePi.getProcessor());
	}

}
