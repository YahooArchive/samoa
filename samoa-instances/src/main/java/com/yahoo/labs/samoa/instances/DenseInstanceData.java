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
public class DenseInstanceData implements InstanceData{

    public DenseInstanceData(double[] array) {
       this.attributeValues = array;
    }
    
    public DenseInstanceData(int length) {
       this.attributeValues = new double[length];
    }
    
    public DenseInstanceData() {
       this(0);
    }
    
    protected double[] attributeValues;

    @Override
    public int numAttributes() {
        return this.attributeValues.length;
    }

    @Override
    public double value(int indexAttribute) {
        return this.attributeValues[indexAttribute];
    }

    @Override
    public boolean isMissing(int indexAttribute) {
       return Double.isNaN(this.value(indexAttribute));
    }

    @Override
    public int numValues() {
        return numAttributes();
    }

    @Override
    public int index(int indexAttribute) {
        return indexAttribute;
    }

    @Override
    public double valueSparse(int indexAttribute) {
        return value(indexAttribute);
    }

    @Override
    public boolean isMissingSparse(int indexAttribute) {
        return isMissing(indexAttribute);
    }

    /*@Override
    public double value(Attribute attribute) {
        return value(attribute.index());
    }*/

    @Override
    public double[] toDoubleArray() {
        return attributeValues.clone();
    }

    @Override
    public void setValue(int attributeIndex, double d) {
        this.attributeValues[attributeIndex] = d;
    }
    
}
