package com.yahoo.labs.samoa.moa.core;

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

import java.util.ArrayList;
import java.util.Collection;

import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.moa.MOAObject;

/**
 * Vector with the capability of automatic expansion.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class AutoExpandVector<T> extends ArrayList<T> implements MOAObject {

    private static final long serialVersionUID = 1L;

    public AutoExpandVector() {
        super(0);
    }
    
    public AutoExpandVector(int size) {
        super(size);
    }

    @Override
    public void add(int pos, T obj) {
        if (pos > size()) {
            while (pos > size()) {
                add(null);
            }
            trimToSize();
        }
        super.add(pos, obj);
    }

    @Override
    public T get(int pos) {
        return ((pos >= 0) && (pos < size())) ? super.get(pos) : null;
    }

    @Override
    public T set(int pos, T obj) {
        if (pos >= size()) {
            add(pos, obj);
            return null;
        }
        return super.set(pos, obj);
    }

    @Override
    public boolean add(T arg0) {
        boolean result = super.add(arg0);
        trimToSize();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> arg0) {
        boolean result = super.addAll(arg0);
        trimToSize();
        return result;
    }

    @Override
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        boolean result = super.addAll(arg0, arg1);
        trimToSize();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        trimToSize();
    }

    @Override
    public T remove(int arg0) {
        T result = super.remove(arg0);
        trimToSize();
        return result;
    }

    @Override
    public boolean remove(Object arg0) {
        boolean result = super.remove(arg0);
        trimToSize();
        return result;
    }

    @Override
    protected void removeRange(int arg0, int arg1) {
        super.removeRange(arg0, arg1);
        trimToSize();
    }

    @Override
    public MOAObject copy() {
        return AbstractMOAObject.copy(this);
    }

    @Override
    public int measureByteSize() {
        return AbstractMOAObject.measureByteSize(this);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }
}
