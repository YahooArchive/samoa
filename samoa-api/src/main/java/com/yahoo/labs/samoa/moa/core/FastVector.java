
/*
 *    FastVector.java

 *
 */
package com.yahoo.labs.samoa.moa.core;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 1999 - 2012 University of Waikato, Hamilton, New Zealand
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

import java.util.ArrayList;

/**
 * Simple extension of ArrayList. Exists for legacy reasons.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 8034 $
 */
public class FastVector<E> extends ArrayList<E> {

    /**
     * Adds an element to this vector. Increases its capacity if its not large
     * enough.
     *
     * @param element the element to add
     */
    public final void addElement(E element) {
        add(element);
    }

    /**
     * Returns the element at the given position.
     *
     * @param index the element's index
     * @return the element with the given index
     */
    public final E elementAt(int index) {
        return get(index);
    }

    /**
     * Deletes an element from this vector.
     *
     * @param index the index of the element to be deleted
     */
    public final void removeElementAt(int index) {
        remove(index);
    }
}
