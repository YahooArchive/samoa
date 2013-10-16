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

import com.yahoo.labs.samoa.moa.AbstractMOAObject;

/**
 * Vector of double numbers with some utilities.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class DoubleVector extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected double[] array;

    public DoubleVector() {
        this.array = new double[0];
    }

    public DoubleVector(double[] toCopy) {
        this.array = new double[toCopy.length];
        System.arraycopy(toCopy, 0, this.array, 0, toCopy.length);
    }

    public DoubleVector(DoubleVector toCopy) {
        this(toCopy.getArrayRef());
    }

    public int numValues() {
        return this.array.length;
    }

    public void setValue(int i, double v) {
        if (i >= this.array.length) {
            setArrayLength(i + 1);
        }
        this.array[i] = v;
    }

    public void addToValue(int i, double v) {
        if (i >= this.array.length) {
            setArrayLength(i + 1);
        }
        this.array[i] += v;
    }

    public void addValues(DoubleVector toAdd) {
        addValues(toAdd.getArrayRef());
    }

    public void addValues(double[] toAdd) {
        if (toAdd.length > this.array.length) {
            setArrayLength(toAdd.length);
        }
        for (int i = 0; i < toAdd.length; i++) {
            this.array[i] += toAdd[i];
        }
    }

    public void subtractValues(DoubleVector toSubtract) {
        subtractValues(toSubtract.getArrayRef());
    }

    public void subtractValues(double[] toSubtract) {
        if (toSubtract.length > this.array.length) {
            setArrayLength(toSubtract.length);
        }
        for (int i = 0; i < toSubtract.length; i++) {
            this.array[i] -= toSubtract[i];
        }
    }

    public void addToValues(double toAdd) {
        for (int i = 0; i < this.array.length; i++) {
            this.array[i] = this.array[i] + toAdd;
        }
    }

    public void scaleValues(double multiplier) {
        for (int i = 0; i < this.array.length; i++) {
            this.array[i] = this.array[i] * multiplier;
        }
    }

    // returns 0.0 for values outside of range
    public double getValue(int i) {
        return ((i >= 0) && (i < this.array.length)) ? this.array[i] : 0.0;
    }

    public double sumOfValues() {
        double sum = 0.0;
        for (double element : this.array) {
            sum += element;
        }
        return sum;
    }

    public int maxIndex() {
        int max = -1;
        for (int i = 0; i < this.array.length; i++) {
            if ((max < 0) || (this.array[i] > this.array[max])) {
                max = i;
            }
        }
        return max;
    }

    public void normalize() {
        scaleValues(1.0 / sumOfValues());
    }

    public int numNonZeroEntries() {
        int count = 0;
        for (double element : this.array) {
            if (element != 0.0) {
                count++;
            }
        }
        return count;
    }

    public double minWeight() {
        if (this.array.length > 0) {
            double min = this.array[0];
            for (int i = 1; i < this.array.length; i++) {
                if (this.array[i] < min) {
                    min = this.array[i];
                }
            }
            return min;
        }
        return 0.0;
    }

    public double[] getArrayCopy() {
        double[] aCopy = new double[this.array.length];
        System.arraycopy(this.array, 0, aCopy, 0, this.array.length);
        return aCopy;
    }

    public double[] getArrayRef() {
        return this.array;
    }

    protected void setArrayLength(int l) {
        double[] newArray = new double[l];
        int numToCopy = this.array.length;
        if (numToCopy > l) {
            numToCopy = l;
        }
        System.arraycopy(this.array, 0, newArray, 0, numToCopy);
        this.array = newArray;
    }

    public void getSingleLineDescription(StringBuilder out) {
        getSingleLineDescription(out, numValues());
    }

    public void getSingleLineDescription(StringBuilder out, int numValues) {
        out.append("{");
        for (int i = 0; i < numValues; i++) {
            if (i > 0) {
                out.append("|");
            }
            out.append(StringUtils.doubleToString(getValue(i), 3));
        }
        out.append("}");
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        getSingleLineDescription(sb);
    }
}
