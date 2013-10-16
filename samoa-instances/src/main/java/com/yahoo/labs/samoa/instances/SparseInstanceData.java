/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yahoo.labs.samoa.instances;

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
 *
 * @author abifet
 */
public class SparseInstanceData implements InstanceData{
    
    public SparseInstanceData(double[] attributeValues, int[] indexValues, int numberAttributes) {
       this.attributeValues = attributeValues;
       this.indexValues = indexValues;
       this.numberAttributes = numberAttributes;
    }
    
    public SparseInstanceData(int length) {
       this.attributeValues = new double[length];
       this.indexValues =  new int[length];
    }
    
    
    protected double[] attributeValues;

    public double[] getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(double[] attributeValues) {
        this.attributeValues = attributeValues;
    }

    public int[] getIndexValues() {
        return indexValues;
    }

    public void setIndexValues(int[] indexValues) {
        this.indexValues = indexValues;
    }

    public int getNumberAttributes() {
        return numberAttributes;
    }

    public void setNumberAttributes(int numberAttributes) {
        this.numberAttributes = numberAttributes;
    }
    protected int[] indexValues;
    protected int numberAttributes;

    @Override
    public int numAttributes() {
        return this.numberAttributes;
    }

    @Override
    public double value(int indexAttribute) {
        int location = locateIndex(indexAttribute);
        //return location == -1 ? 0 : this.attributeValues[location];
      //      int index = locateIndex(attIndex);
    if ((location >= 0) && (indexValues[location] == indexAttribute)) {
      return attributeValues[location];
    } else {
      return 0.0;
    }
    }

    @Override
    public boolean isMissing(int indexAttribute) {
        return Double.isNaN(this.value(indexAttribute));
    }

    @Override
    public int numValues() {
        return this.attributeValues.length;
    }

    @Override
    public int index(int indexAttribute) {
        return this.indexValues[indexAttribute];
    }

    @Override
    public double valueSparse(int indexAttribute) {
        return this.attributeValues[indexAttribute];
    }

    @Override
    public boolean isMissingSparse(int indexAttribute) {
        return Double.isNaN(this.valueSparse(indexAttribute));
    }

    /*@Override
    public double value(Attribute attribute) {
        return value(attribute.index());
    }*/

    @Override
    public double[] toDoubleArray() {
        double[] array = new double[numAttributes()];
        for (int i=0; i<numValues() ; i++) {
            array[index(i)] = valueSparse(i);
        }
        return array;
    }

    @Override
    public void setValue(int attributeIndex, double d) {
        int index = locateIndex(attributeIndex);
        if (index(index) == attributeIndex) {
            this.attributeValues[index] = d;
        } else {
            // We need to add the value
        }
    }
    
    /**
   * Locates the greatest index that is not greater than the given index.
   * 
   * @return the internal index of the attribute index. Returns -1 if no index
   *         with this property could be found
   */
  public int locateIndex(int index) {

    int min = 0;
    int max = this.indexValues.length - 1;

    if (max == -1) {
      return -1;
    }

    // Binary search
    while ((this.indexValues[min] <= index) && (this.indexValues[max] >= index)) {
      int current = (max + min) / 2;
      if (this.indexValues[current] > index) {
        max = current - 1;
      } else if (this.indexValues[current] < index) {
        min = current + 1;
      } else {
        return current;
      }
    }
    if (this.indexValues[max] < index) {
      return max;
    } else {
      return min - 1;
    }
  }
    
}
