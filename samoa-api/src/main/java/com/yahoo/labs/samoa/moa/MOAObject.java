package com.yahoo.labs.samoa.moa;

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

import java.io.Serializable;

/**
 * Interface implemented by classes in MOA, so that all are serializable,
 * can produce copies of their objects, and can measure its memory size.
 * They also give a string description. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public interface MOAObject extends Serializable {

    /**
     * Gets the memory size of this object.
     *
     * @return the memory size of this object
     */
    public int measureByteSize();

    /**
     * This method produces a copy of this object.
     *
     * @return a copy of this object
     */
    public MOAObject copy();

    /**
     * Returns a string representation of this object.
     * Used in <code>AbstractMOAObject.toString</code>
     * to give a string representation of the object.
     *
     * @param sb	the stringbuilder to add the description
     * @param indent	the number of characters to indent
     */
    public void getDescription(StringBuilder sb, int indent);
}
