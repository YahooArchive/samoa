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

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author abifet
 */
public class Instances implements Serializable {

	public static final String ARFF_RELATION = "@relation";
	public static final String ARFF_DATA = "@data";

	
	protected InstanceInformation instanceInformation;
	/**
	 * The instances.
	 */
	protected List<Instance> instances;

	transient protected ArffLoader arff;
        
	protected int classAttribute;

	public Instances(InstancesHeader modelContext) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Instances(Instances chunk) {
		this.instanceInformation = chunk.instanceInformation();
		// this.relationName = chunk.relationName;
		// this.attributes = chunk.attributes;
		this.instances = chunk.instances;
	}

	public Instances() {
		// this.instanceInformation = chunk.instanceInformation();
		// this.relationName = chunk.relationName;
		// this.attributes = chunk.attributes;
		// this.instances = chunk.instances;
	}

	public Instances(Reader reader, int size, int classAttribute) {
                this.classAttribute = classAttribute;
		arff = new ArffLoader(reader, 0, classAttribute);
		this.instanceInformation = arff.getStructure();
		this.instances = new ArrayList<>();
	}

	public Instances(Instances chunk, int capacity) {
		this(chunk);
	}

	public Instances(String st, List<Attribute> v, int capacity) {
		
		this.instanceInformation = new InstanceInformation(st, v);
		this.instances = new ArrayList<>();
	}

	public Instances(Instances chunk, int i, int j) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Instances(StringReader st, int v) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	// Information Instances
	public void setRelationName(String string) {
		this.instanceInformation.setRelationName(string);
	}

	public String getRelationName() {
		return this.instanceInformation.getRelationName();
	}

	public int classIndex() {
		return this.instanceInformation.classIndex();
	}

	public void setClassIndex(int classIndex) {
		this.instanceInformation.setClassIndex(classIndex);
	}

	public Attribute classAttribute() {
		return this.instanceInformation.classAttribute();
	}

	public int numAttributes() {
		return this.instanceInformation.numAttributes();
	}

	public Attribute attribute(int w) {
		return this.instanceInformation.attribute(w);
	}

	public int numClasses() {
		return this.instanceInformation.numClasses();
	}

	public void deleteAttributeAt(Integer integer) {
		this.instanceInformation.deleteAttributeAt(integer);
	}

	public void insertAttributeAt(Attribute attribute, int i) {
		this.instanceInformation.insertAttributeAt(attribute, i);
	}

	// List of Instances
	public Instance instance(int num) {
		return this.instances.get(num);
	}

	public int numInstances() {
		return this.instances.size();
	}

	public void add(Instance inst) {
		this.instances.add(inst.copy());
	}

	public void randomize(Random random) {
		for (int j = numInstances() - 1; j > 0; j--) {
			swap(j, random.nextInt(j + 1));
		}
	}

	public void stratify(int numFolds) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Instances trainCV(int numFolds, int n, Random random) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public Instances testCV(int numFolds, int n) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/*
	 * public Instances dataset() { throw new
	 * UnsupportedOperationException("Not yet implemented"); }
	 */
	public double meanOrMode(int j) {
		throw new UnsupportedOperationException("Not yet implemented"); // CobWeb
	}

	public boolean readInstance(Reader fileReader) {

		// ArffReader arff = new ArffReader(reader, this, m_Lines, 1);
            if (arff == null) {
               arff = new ArffLoader(fileReader,0,this.classAttribute); 
            }
		Instance inst = arff.readInstance(fileReader);
		if (inst != null) {
			inst.setDataset(this);
			add(inst);
			return true;
		} else {
			return false;
		}
	}

	public void delete() {
		this.instances = new ArrayList<>();
	}

	public void swap(int i, int j) {
		Instance in = instances.get(i);
		instances.set(i, instances.get(j));
		instances.set(j, in);
	}

	private InstanceInformation instanceInformation() {
		return this.instanceInformation;
	}

	public Attribute attribute(String name) {

		for (int i = 0; i < numAttributes(); i++) {
			if (attribute(i).name().equals(name)) {
				return attribute(i);
			}
		}
		return null;
	}


	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();

		for (int i = 0; i < numInstances(); i++) {
			text.append(instance(i).toString());
			if (i < numInstances() - 1) {
				text.append('\n');
			}
		}
		return text.toString();
	}

	// toString() with header
	public String toStringArff() {
		StringBuilder text = new StringBuilder();

		text.append(ARFF_RELATION).append(" ")
				.append(Utils.quote(getRelationName())).append("\n\n");
		for (int i = 0; i < numAttributes(); i++) {
			text.append(attribute(i).toString()).append("\n");
		}
		text.append("\n").append(ARFF_DATA).append("\n");

		text.append(toString());
		return text.toString();

	}
}
