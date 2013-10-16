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
public interface InstanceData extends Serializable{

    public int numAttributes();

    public double value(int instAttIndex);

    public boolean isMissing(int instAttIndex);

    public int numValues();

    public int index(int i);

    public double valueSparse(int i);

    public boolean isMissingSparse(int p1);

    //public double value(Attribute attribute);

    public double[] toDoubleArray();

    public void setValue(int m_numAttributes, double d);
    
}
