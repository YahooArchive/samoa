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
public class DenseInstance extends SingleLabelInstance {

	private static final long serialVersionUID = 280360594027716737L;

	public DenseInstance() {
		// necessary for kryo serializer
	}
	
    public DenseInstance(double weight, double[] res) {
         super(weight,res);
    }
    public DenseInstance(SingleLabelInstance inst) {
        super(inst);
    }
    
    public DenseInstance(Instance inst) {
        super((SingleLabelInstance) inst);
    }
    public DenseInstance(double numberAttributes) {
         super((int) numberAttributes);
         //super(1, new double[(int) numberAttributes-1]); 
         //Add missing values
         //for (int i = 0; i < numberAttributes-1; i++) {
          //   //this.setValue(i, Double.NaN);
        //}
         
    }
    
    @Override
    public String toString() {
    	 StringBuffer text = new StringBuffer();

    	    for (int i = 0; i < this.instanceInformation.numAttributes(); i++) {
    	      if (i > 0)
    	        text.append(",");
    	      text.append(this.value(i));
    	    }
    	    text.append(",").append(this.weight());
  	      
    	    return text.toString();
    }
}
