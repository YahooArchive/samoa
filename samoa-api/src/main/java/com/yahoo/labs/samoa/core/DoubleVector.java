package com.yahoo.labs.samoa.core;

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

import java.util.Arrays;

import com.google.common.primitives.Doubles;

public class DoubleVector implements java.io.Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8243012708860261398L;

    private double[] doubleArray;

    public DoubleVector() {
        this.doubleArray = new double[0];
    }

    public DoubleVector(double[] toCopy) {
        this.doubleArray = new double[toCopy.length];
        System.arraycopy(toCopy, 0, this.doubleArray, 0, toCopy.length);
    }

    public DoubleVector(DoubleVector toCopy) {
        this(toCopy.getArrayRef());
    }

    public double[] getArrayRef() {
        return this.doubleArray;
    }

    public double[] getArrayCopy() {
        return Doubles.concat(this.doubleArray);
    }

    public int numNonZeroEntries() {
        int count = 0;
        for (double element : this.doubleArray) {
            if (Double.compare(element, 0.0) != 0) {
                count++;
            }
        }
        return count;
    }

    public void setValue(int index, double value) {
        if (index >= doubleArray.length) {
            this.doubleArray = Doubles.ensureCapacity(this.doubleArray, index + 1, 0);
        }
        this.doubleArray[index] = value;
    }

    public void addToValue(int index, double value) {
        if (index >= doubleArray.length) {
            this.doubleArray = Doubles.ensureCapacity(this.doubleArray, index + 1, 0);
        }
        this.doubleArray[index] += value;
    }

    public double sumOfValues() {
        double sum = 0.0;
        for (double element : this.doubleArray) {
            sum += element;
        }
        return sum;
    }

    public void getSingleLineDescription(StringBuilder out) {
        out.append("{");
        out.append(Doubles.join("|", this.doubleArray));
        out.append("}");
    }

    @Override
    public String toString() {
        return "DoubleVector [doubleArray=" + Arrays.toString(doubleArray) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(doubleArray);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DoubleVector))
            return false;
        DoubleVector other = (DoubleVector) obj;
        return Arrays.equals(doubleArray, other.doubleArray);
    }
}
