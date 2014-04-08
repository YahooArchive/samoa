package com.yahoo.labs.samoa.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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

import com.yahoo.labs.samoa.topology.IProcessingItem;

/**
 * Common interface of SamzaEntranceProcessingItem and
 * SamzaProcessingItem
 * 
 * @author Anh Thu Vu
 */
public interface SamzaProcessingNode extends IProcessingItem {
	/**
	 * Registers an output stream with this processing item
	 * 
	 * @param stream
	 *               the output stream
	 * @return the number of output streams of this processing item
	 */
	public int addOutputStream(SamzaStream stream);
	
	/**
	 * Gets the name/id of this processing item
	 * 
	 * @return the name/id of this processing item
	 */
	// TODO: include getName() and setName() in IProcessingItem and/or AbstractEPI/PI
	public String getName();
	
	/**
	 * Sets the name/id for this processing item
	 * @param name
	 *            the name/id of this processing item
	 */
	// TODO: include getName() and setName() in IProcessingItem and/or AbstractEPI/PI
	public void setName(String name);
}