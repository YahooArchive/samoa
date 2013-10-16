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
public class SparseInstance extends SingleLabelInstance{
    
    public SparseInstance(double d, double[] res) {
         super(d,res);
    }
    public SparseInstance(SingleLabelInstance inst) {
        super(inst);
    }

    public SparseInstance(double numberAttributes) {
      //super(1, new double[(int) numberAttributes-1]); 
      super(1,null,null,(int) numberAttributes);  
    }
    
    public SparseInstance(double weight, double[] attributeValues, int[] indexValues, int numberAttributes) {
        super(weight,attributeValues,indexValues,numberAttributes);
    }
    
}
