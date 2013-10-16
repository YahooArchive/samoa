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
public class SingleClassInstanceData implements InstanceData {

    protected double classValue;
    
    @Override
    public int numAttributes() {
        return 1;
    }

    @Override
    public double value(int instAttIndex) {
        return classValue;
    }

    @Override
    public boolean isMissing(int indexAttribute) {
        return Double.isNaN(this.value(indexAttribute));
    }

    @Override
    public int numValues() {
        return 1;
    }

    @Override
    public int index(int i) {
        return 0;
    }

    @Override
    public double valueSparse(int i) {
        return value(i);
    }

    @Override
    public boolean isMissingSparse(int indexAttribute) {
        return Double.isNaN(this.value(indexAttribute));
    }

    /*@Override
    public double value(Attribute attribute) {
        return this.classValue;
    }*/

    @Override
    public double[] toDoubleArray() {
        double[] array = {this.classValue};
        return array;
    }

    @Override
    public void setValue(int m_numAttributes, double d) {
        this.classValue = d;
    }
    
}
