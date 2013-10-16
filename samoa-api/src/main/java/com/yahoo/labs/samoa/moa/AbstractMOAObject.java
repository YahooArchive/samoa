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

import com.yahoo.labs.samoa.moa.core.SerializeUtils;
//import moa.core.SizeOf;

/**
 * Abstract MOA Object. All classes that are serializable, copiable,
 * can measure its size, and can give a description, extend this class.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public abstract class AbstractMOAObject implements MOAObject {

    @Override
    public MOAObject copy() {
        return copy(this);
    }

    @Override
    public int measureByteSize() {
        return measureByteSize(this);
    }

    /**
     * Returns a description of the object.
     *
     * @return a description of the object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        getDescription(sb, 0);
        return sb.toString();
    }

    /**
     * This method produces a copy of an object.
     *
     * @param obj object to copy
     * @return a copy of the object
     */
    public static MOAObject copy(MOAObject obj) {
        try {
            return (MOAObject) SerializeUtils.copyObject(obj);
        } catch (Exception e) {
            throw new RuntimeException("Object copy failed.", e);
        }
    }

    /**
     * Gets the memory size of an object.
     *
     * @param obj object to measure the memory size
     * @return the memory size of this object
     */
    public static int measureByteSize(MOAObject obj) {
        return 0; //(int) SizeOf.fullSizeOf(obj);
    }
}
