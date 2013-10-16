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
// public int[] m_AttValues; // for DataPoint

public class SingleLabelInstance implements Instance {

	protected double weight;

	protected InstanceData instanceData;

	protected InstanceData classData;

	// Fast implementation without using Objects
	// protected double[] attributeValues;
	// protected double classValue;

	protected InstancesHeader instanceInformation;

	public SingleLabelInstance() {
		// necessary for kryo serializer
	}

	public SingleLabelInstance(SingleLabelInstance inst) {
		this.weight = inst.weight;
		this.instanceData = inst.instanceData; // copy
		this.classData = inst.classData; // copy
		// this.classValue = inst.classValue;
		// this.attributeValues = inst.attributeValues;
		this.instanceInformation = inst.instanceInformation;
	}

	// Dense
	public SingleLabelInstance(double weight, double[] res) {
		this.weight = weight;
		this.instanceData = new DenseInstanceData(res);
		//this.attributeValues = res;
		this.classData = new SingleClassInstanceData();
		// this.classValue = Double.NaN;
		
		
	}

	// Sparse
	public SingleLabelInstance(double weight, double[] attributeValues,
			int[] indexValues, int numberAttributes) {
		this.weight = weight;
		this.instanceData = new SparseInstanceData(attributeValues,
				indexValues, numberAttributes); // ???
		this.classData = new SingleClassInstanceData();
		// this.classValue = Double.NaN;
		//this.instanceInformation = new InstancesHeader();
		
	}

	public SingleLabelInstance(double weight, InstanceData instanceData) {
		this.weight = weight;
		this.instanceData = instanceData; // ???
		// this.classValue = Double.NaN;
		this.classData = new SingleClassInstanceData();
		//this.instanceInformation = new InstancesHeader();
	}

	public SingleLabelInstance(int numAttributes) {
		this.instanceData = new DenseInstanceData(new double[numAttributes]);
		// m_AttValues = new double[numAttributes];
		/*
		 * for (int i = 0; i < m_AttValues.length; i++) { m_AttValues[i] =
		 * Utils.missingValue(); }
		 */
		this.weight = 1;
		this.classData = new SingleClassInstanceData();
		this.instanceInformation = new InstancesHeader();
	}

	@Override
	public double weight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public Attribute attribute(int instAttIndex) {
		return this.instanceInformation.attribute(instAttIndex);
	}

	@Override
	public void deleteAttributeAt(int i) {
		// throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void insertAttributeAt(int i) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int numAttributes() {
		return this.instanceInformation.numAttributes();
	}

	@Override
	public double value(int instAttIndex) {
		return // attributeValues[instAttIndex]; //
		this.instanceData.value(instAttIndex);
	}

	@Override
	public boolean isMissing(int instAttIndex) {
		return // Double.isNaN(value(instAttIndex)); //
		this.instanceData.isMissing(instAttIndex);
	}

	@Override
	public int numValues() {
		return // this.attributeValues.length; //
		this.instanceData.numValues();
	}

	@Override
	public int index(int i) {
		return // i; //
		this.instanceData.index(i);
	}

	@Override
	public double valueSparse(int i) {
		return this.instanceData.valueSparse(i);
	}

	@Override
	public boolean isMissingSparse(int p) {
		return this.instanceData.isMissingSparse(p);
	}

	@Override
	public double value(Attribute attribute) {
		// throw new UnsupportedOperationException("Not yet implemented");
		// //Predicates.java
		return value(attribute.index());

	}

	@Override
	public String stringValue(int i) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public double[] toDoubleArray() {
		return // this.attributeValues; //
		this.instanceData.toDoubleArray();
	}

	@Override
	public void setValue(int numAttribute, double d) {
		this.instanceData.setValue(numAttribute, d);
		// this.attributeValues[numAttribute] = d;
	}

	@Override
	public double classValue() {
		return this.classData.value(0);
		// return classValue;
	}

	@Override
	public int classIndex() {
		return instanceInformation.classIndex();
	}

	@Override
	public int numClasses() {
		return this.instanceInformation.numClasses();
	}

	@Override
	public boolean classIsMissing() {
		return // Double.isNaN(this.classValue);//
		this.classData.isMissing(0);
	}

	@Override
	public Attribute classAttribute() {
		return this.instanceInformation.attribute(0);
	}

	@Override
	public void setClassValue(double d) {
		this.classData.setValue(0, d);
		// this.classValue = d;
	}

	@Override
	public Instance copy() {
		SingleLabelInstance inst = new SingleLabelInstance(this);
		return inst;
	}

	@Override
	public Instances dataset() {
		return this.instanceInformation;
	}

	@Override
	public void setDataset(Instances dataset) {
		this.instanceInformation = new InstancesHeader(dataset);
	}

	public void addSparseValues(int[] indexValues, double[] attributeValues,
			int numberAttributes) {
		this.instanceData = new SparseInstanceData(attributeValues,
				indexValues, numberAttributes); // ???
	}

	@Override
	public String toString() {
		StringBuffer text = new StringBuffer();

		for (int i = 0; i < this.numValues() ; i++) {
			if (i > 0)
				text.append(",");
			text.append(this.value(i));
		}
		text.append(",").append(this.weight());

		return text.toString();
	}

}
