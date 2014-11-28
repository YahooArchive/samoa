package com.yahoo.labs.samoa.topology;

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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.EntranceProcessor;

/**
 * Implementation of EntranceProcessingItem for local engines (Simple, Multithreads)
 * 
 * @author Anh Thu Vu
 *
 */
public class LocalEntranceProcessingItem extends AbstractEntranceProcessingItem {
	public LocalEntranceProcessingItem(EntranceProcessor processor) {
		super(processor);
	}
	
	/**
	 * If there are available events, first event in the queue will be
	 * sent out on the output stream. 
	 * @return true if there is (at least) one available event and it was sent out
	 *         false otherwise 
	 */
	public boolean injectNextEvent() {
		if (this.getProcessor().hasNext()) {
			ContentEvent event = this.getProcessor().nextEvent();
			this.getOutputStream().put(event);
			return true;
		}
		return false;
	}

	/**
	 * Start sending events by calling {@link #injectNextEvent()}. If there are no available events, 
	 * and that the stream is not entirely consumed, it will wait by calling
     * {@link #waitForNewEvents()} before attempting to send again.
     * </p>
     * When the stream is entirely consumed, the last event is tagged accordingly and the processor gets the
     * finished status.
     *
	 */
	public void startSendingEvents () {
		if (this.getOutputStream() == null) 
			throw new IllegalStateException("Try sending events from EntrancePI while outputStream is not set.");
		
		while (!this.getProcessor().isFinished()) {
            if (!this.injectNextEvent()) {
                try {
                    waitForNewEvents();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
	}
	
	/**
	 * Method to wait for an amount of time when there are no available events.
	 * Implementation of EntranceProcessingItem should override this method to 
	 * implement non-blocking wait or to adjust the amount of time.
	 */
	protected void waitForNewEvents() throws Exception {
		Thread.sleep(100);
	}
}
