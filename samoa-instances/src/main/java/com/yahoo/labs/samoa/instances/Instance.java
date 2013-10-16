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

import java.io.Serializable;

/**
 *
 * @author abifet
 */

public interface Instance extends Serializable{

    double weight();
    void setWeight(double weight);
    
    //Attributes
    Attribute attribute(int instAttIndex);
    void deleteAttributeAt(int i);
    void insertAttributeAt(int i);
    int numAttributes();
    public void addSparseValues(int[] indexValues, double[] attributeValues, int numberAttributes);
    

    //Values
    int numValues();
    String stringValue(int i);
    double value(int instAttIndex);
    double value(Attribute attribute);
    void setValue(int m_numAttributes, double d);
    boolean isMissing(int instAttIndex);
    int index(int i);
    double valueSparse(int i);
    boolean isMissingSparse(int p1);
    double[] toDoubleArray();
    
    //Class
    Attribute classAttribute();
    int classIndex();
    boolean classIsMissing();
    double classValue();
    int numClasses();
    void setClassValue(double d);

    Instance copy();

    //Dataset
    void setDataset(Instances dataset);
    Instances dataset();
    String toString();
}

