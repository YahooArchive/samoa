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

import com.yahoo.labs.samoa.core.EntranceProcessor;

/**
 * Entrance processing item interface.
 */
public interface EntranceProcessingItem extends IProcessingItem {

    @Override
    /**
     * Gets the processing item processor.
     * 
     * @return the embedded EntranceProcessor. 
     */
    public EntranceProcessor getProcessor();

    /**
     * Set the single output stream for this EntranceProcessingItem.
     * 
     * @param stream
     *            the stream
     * @return the current instance of the EntranceProcessingItem for fluent interface.
     */
    public EntranceProcessingItem setOutputStream(Stream stream);
}