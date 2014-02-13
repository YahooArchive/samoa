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
import mockit.NonStrictExpectations;
import mockit.Tested;

import org.junit.Before;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * @author Anh Thu Vu
 *
 */
public class ThreadsEntranceProcessingItemTest {

	@Tested private ThreadsEntranceProcessingItem entrancePi;
	
	@Mocked private EntranceProcessor entranceProcessor;
	@Mocked private Stream outputStream, anotherStream;
	@Mocked private ContentEvent event;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		entrancePi = new ThreadsEntranceProcessingItem(entranceProcessor);
	}

	@Test
	public void testContructor() {
		assertSame("EntranceProcessor is not set correctly.",entranceProcessor,entrancePi.getProcessor());
	}
	
	@Test
	public void testSetOutputStream() {
		entrancePi.setOutputStream(outputStream);
		assertSame("OutoutStream is not set correctly.",outputStream,entrancePi.getOutputStream());
	}
	
	@Test
	public void testSetOutputStreamRepeate() {
		entrancePi.setOutputStream(outputStream);
		entrancePi.setOutputStream(outputStream);
		assertSame("OutoutStream is not set correctly.",outputStream,entrancePi.getOutputStream());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetOutputStreamError() {
		entrancePi.setOutputStream(outputStream);
		entrancePi.setOutputStream(anotherStream);
	}
	
	// TODO: check that if entranceProcessor.hasNext() == false, event.isLast==true
	@Test
	public void testInjectNextEvent() {
		entrancePi.setOutputStream(outputStream);
		
		new NonStrictExpectations() {
			{
				entranceProcessor.hasNext();
			}
		};
		new Expectations() {
			{
				entranceProcessor.nextEvent();
				result=event; times=1;
				
				outputStream.put(event); times=1;
			}
		};
		entrancePi.injectNextEvent();
	}

}
