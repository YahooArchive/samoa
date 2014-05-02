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

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import org.junit.Test;

/**
 * @author Anh Thu Vu
 *
 */
public class SimpleEngineTest {

	@Tested private SimpleEngine unused;
	@Mocked private SimpleTopology topology;
	@Mocked private Runtime mockedRuntime;
	
	@Test
	public void testSubmitTopology() {
		new NonStrictExpectations() {
			{
				Runtime.getRuntime();
				result=mockedRuntime;
				mockedRuntime.exit(0);
			}
		};
		SimpleEngine.submitTopology(topology);
		new Verifications() {
			{
				topology.run();
			}
		};
	}

}
