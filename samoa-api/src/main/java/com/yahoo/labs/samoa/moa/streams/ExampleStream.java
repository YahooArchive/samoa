package com.yahoo.labs.samoa.moa.streams;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
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

import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.moa.MOAObject;
import com.yahoo.labs.samoa.moa.core.Example;

/**
 * Interface representing a data stream of examples. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface ExampleStream<E extends Example> extends MOAObject {

    /**
     * Gets the header of this stream.
     * This is useful to know attributes and classes.
     * InstancesHeader is an extension of weka.Instances.
     *
     * @return the header of this stream
     */
    public InstancesHeader getHeader();

    /**
     * Gets the estimated number of remaining instances in this stream
     *
     * @return the estimated number of instances to get from this stream
     */
    public long estimatedRemainingInstances();

    /**
     * Gets whether this stream has more instances to output.
     * This is useful when reading streams from files.
     *
     * @return true if this stream has more instances to output
     */
    public boolean hasMoreInstances();

    /**
     * Gets the next example from this stream.
     *
     * @return the next example of this stream
     */
    public E nextInstance();

    /**
     * Gets whether this stream can restart.
     *
     * @return true if this stream can restart
     */
    public boolean isRestartable();

    /**
     * Restarts this stream. It must be similar to
     * starting a new stream from scratch.
     *
     */
    public void restart();
}
