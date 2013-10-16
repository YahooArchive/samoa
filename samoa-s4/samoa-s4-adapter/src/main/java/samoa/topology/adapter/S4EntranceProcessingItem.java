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

import org.apache.s4.core.App;
import org.apache.s4.core.ProcessingElement;

import samoa.core.Processor;
import samoa.topology.EntranceProcessingItem;
import samoa.topology.impl.DoTaskApp;
import weka.core.Instance;

public class S4EntranceProcessingItem extends ProcessingElement implements EntranceProcessingItem {

	private Processor processor;
	//DoTaskApp app;
	
	
	public S4EntranceProcessingItem(App app){
		super(app);
		//this.app = (DoTaskApp) app;
		this.setSingleton(true);
		
	}

	@Override
	public Processor getProcessor() {
		return this.processor;
	}

	@Override
	public void put(Instance inst) {
		// do nothing
		//may not needed

	}

	@Override
	protected void onCreate() {
		
		//		if (this.processor != null){
//			this.processor = this.processor.newProcessor(this.processor);
//			this.processor.onCreate(Integer.parseInt(getId()));
//		}
	}

	@Override
	protected void onRemove() {
		//do nothing
		
	}
	
	public void setProcessor(Processor processor){
		this.processor = processor;
	}
}
