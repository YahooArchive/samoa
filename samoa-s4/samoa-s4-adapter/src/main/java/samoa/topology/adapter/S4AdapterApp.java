package samoa.topology.adapter;

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

import org.apache.s4.core.adapter.AdapterApp;

import samoa.sandbox.SourceProcessor;
import samoa.streams.StreamSourceProcessor;

public class S4AdapterApp extends AdapterApp {

	S4EntranceProcessingItem entrancePI;
	StreamSourceProcessor sourceProcessor;
	
	@Override
	protected void onInit() {
		entrancePI = new S4EntranceProcessingItem(this);
		sourceProcessor = new StreamSourceProcessor();
		entrancePI.setProcessor(sourceProcessor);
	}
	
	@Override
	protected void onStart() {
		
	}
	
}
