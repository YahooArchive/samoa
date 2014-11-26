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
import mockit.StrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * @author Anh Thu Vu
 *
 */
public class SimpleEntranceProcessingItemTest {

	@Tested private SimpleEntranceProcessingItem entrancePi;
	
	@Mocked private EntranceProcessor entranceProcessor;
	@Mocked private Stream outputStream, anotherStream;
	@Mocked private ContentEvent event;
	
	@Mocked private Thread unused;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		entrancePi = new SimpleEntranceProcessingItem(entranceProcessor);
	}

	@Test
	public void testContructor() {
		assertSame("EntranceProcessor is not set correctly.",entranceProcessor,entrancePi.getProcessor());
	}
	
	@Test
	public void testSetOutputStream() {
		entrancePi.setOutputStream(outputStream);
		assertSame("OutputStream is not set correctly.",outputStream,entrancePi.getOutputStream());
	}
	
	@Test
	public void testSetOutputStreamRepeate() {
		entrancePi.setOutputStream(outputStream);
		entrancePi.setOutputStream(outputStream);
		assertSame("OutputStream is not set correctly.",outputStream,entrancePi.getOutputStream());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSetOutputStreamError() {
		entrancePi.setOutputStream(outputStream);
		entrancePi.setOutputStream(anotherStream);
	}
	
	@Test
	public void testInjectNextEventSuccess() {
		entrancePi.setOutputStream(outputStream);
		new StrictExpectations() {
			{
				entranceProcessor.hasNext();
				result=true;
				
				entranceProcessor.nextEvent();
				result=event;
			}
		};
		entrancePi.injectNextEvent();
		new Verifications() {
			{
				outputStream.put(event);
			}
		};
	}
	
	@Test
	public void testStartSendingEvents() {
		entrancePi.setOutputStream(outputStream);
		new StrictExpectations() {
			{
				for (int i=0; i<1; i++) {
					entranceProcessor.isFinished(); result=false;
					entranceProcessor.hasNext(); result=false;
				}
				
				for (int i=0; i<5; i++) {
					entranceProcessor.isFinished(); result=false;
					entranceProcessor.hasNext(); result=true;
					entranceProcessor.nextEvent(); result=event;
					outputStream.put(event);
				}
				
				for (int i=0; i<2; i++) {
					entranceProcessor.isFinished(); result=false;
					entranceProcessor.hasNext(); result=false;
				}
				
				for (int i=0; i<5; i++) {
					entranceProcessor.isFinished(); result=false;
					entranceProcessor.hasNext(); result=true;
					entranceProcessor.nextEvent(); result=event;
					outputStream.put(event);
				}

				entranceProcessor.isFinished(); result=true; times=1;
				entranceProcessor.hasNext(); times=0;
			}
		};
		entrancePi.startSendingEvents();
		new Verifications() {
			{
				try {
					Thread.sleep(anyInt); times=3;
				} catch (InterruptedException e) {

				}
			}
		};
	}
	
	@Test(expected=IllegalStateException.class)
	public void testStartSendingEventsError() {
		entrancePi.startSendingEvents();
	}

}
