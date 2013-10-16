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

/**
 * License
 */

import net.jcip.annotations.Immutable;

import org.apache.s4.base.Event;

import com.yahoo.labs.samoa.core.ContentEvent;

/**
 * The Class InstanceEvent.
 */
@Immutable
final public class S4Event extends Event {

	private String key;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/** The content event. */
	private ContentEvent contentEvent;
	
	/**
	 * Instantiates a new instance event.
	 */
	public S4Event() {
		// Needed for serialization of kryo
	}

	/**
	 * Instantiates a new instance event.
	 *
	 * @param contentEvent the content event
	 */
	public S4Event(ContentEvent contentEvent) {
		if (contentEvent != null) {
			this.contentEvent = contentEvent;
			this.key = contentEvent.getKey();
			
		}
	}

	/**
	 * Gets the content event.
	 *
	 * @return the content event
	 */
	public ContentEvent getContentEvent() {
		return contentEvent;
	}

	/**
	 * Sets the content event.
	 *
	 * @param contentEvent the new content event
	 */
	public void setContentEvent(ContentEvent contentEvent) {
		this.contentEvent = contentEvent;
	}

}
