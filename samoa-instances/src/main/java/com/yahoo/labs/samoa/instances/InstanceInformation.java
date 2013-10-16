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
import java.util.List;

/**
 *
 * @author abifet
 */
public class InstanceInformation implements Serializable{
    
    //Should we split Instances as a List of Instances, and InformationInstances
    
  /** The dataset's name. */
  protected String relationName;         

  /** The attribute information. */
  protected List<Attribute> attributes;
  
  protected int classIndex;
  

 
    public InstanceInformation(InstanceInformation chunk) {
        this.relationName = chunk.relationName;
        this.attributes = chunk.attributes;
        this.classIndex = chunk.classIndex;
    }
    
    public InstanceInformation(String st, List<Attribute> v) {
        this.relationName = st;
        this.attributes = v;
    }
    
    public InstanceInformation() {
        this.relationName = null;
        this.attributes = null;
    }
    
    
    //Information Instances
    
    public void setRelationName(String string) {
        this.relationName = string;
    }

    public String getRelationName() {
        return this.relationName;
    }
    
    public int classIndex() {
        return classIndex; 
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }
  
    public Attribute classAttribute() {
        return this.attribute(this.classIndex());
    }

    public int numAttributes() {
        return this.attributes.size();
    }

    public Attribute attribute(int w) {
        return this.attributes.get(w);
    }
    
    public int numClasses() {
        return this.attributes.get(this.classIndex()).numValues();
    }
    
    public void deleteAttributeAt(Integer integer) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void insertAttributeAt(Attribute attribute, int i) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setAttributes(List<Attribute> v) {
        this.attributes = v;
    }
    
    
}
