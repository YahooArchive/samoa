package com.yahoo.labs.samoa.topology;

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

import com.yahoo.labs.samoa.core.Processor;

/**
 * ProcessingItem interface specific for entrance processing items.
 * 
 * @author severien
 *
 */
public interface IProcessingItem {
	
	/**
	 * Gets the processing item processor.
	 * 
	 * @return Processor
	 */
	public Processor getProcessor();
	
	/**
	 * Sets processing item name.
	 * 
	 * @param name
	 */
	//public void setName(String name);

}
