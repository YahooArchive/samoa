package com.yahoo.labs.samoa.core;

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

import com.google.common.primitives.Doubles;

public class DoubleVector implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8243012708860261398L;
	
	private double[] doubleArray;
	
	public DoubleVector(){
		this.doubleArray = new double[0];
	}
	
	public DoubleVector(double[] toCopy){
		this.doubleArray = new double[toCopy.length];
		System.arraycopy(toCopy, 0, this.doubleArray, 0, toCopy.length);
	}
	
	public DoubleVector(DoubleVector toCopy){
		this(toCopy.getArrayRef());
	}
	
	public double[] getArrayRef(){
		return this.doubleArray;
	}
	
	public double[] getArrayCopy(){
		return Doubles.concat(this.doubleArray);
	}
	
	public int numNonZeroEntries(){
		int count = 0;
		for(double element: this.doubleArray){
			if(element != 0.0){
				count++;
			}
		}
		return count;
	}
		
	public void setValue(int i, double v){
		if(i >= doubleArray.length){
			this.doubleArray = Doubles.ensureCapacity(this.doubleArray, i+1, 0);
		}
		this.doubleArray[i] = v;
	}
	
	public void addToValue(int i, double v){
		if(i >= doubleArray.length){
			this.doubleArray = Doubles.ensureCapacity(this.doubleArray, i+1, 0);
		}
		this.doubleArray[i] += v;
	}
	
	public double sumOfValues(){
		double sum = 0.0;
		for (double element: this.doubleArray){
			sum += element;
		}
		return sum;
	}
	
	public void getSingleLineDescription(StringBuilder out){
		out.append("{");
		out.append(Doubles.join("|", this.doubleArray));
		out.append("}");
	}
	
}
