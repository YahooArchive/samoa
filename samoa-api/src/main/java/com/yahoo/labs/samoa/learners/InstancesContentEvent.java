
package com.yahoo.labs.samoa.learners;

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
 * License
 */

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.SerializableInstance;
import net.jcip.annotations.Immutable;
import com.yahoo.labs.samoa.instances.Instance;
import java.util.LinkedList;
import java.util.List;
//import weka.core.Instance;


/**
 * The Class InstanceEvent.
 */
@Immutable
final public class InstancesContentEvent implements ContentEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8620668863064613845L;
	private long instanceIndex;
	private int classifierIndex;
	private int evaluationIndex;
	//private SerializableInstance instance;
	private boolean isTraining;
	private boolean isTesting;
	private boolean isLast = false;
	
	public InstancesContentEvent() {
		
	}

	/**
	 * Instantiates a new instance event.
	 *
	 * @param index the index
	 * @param instance the instance
	 * @param isTraining the is training
	 */
	public InstancesContentEvent(long index,// Instance instance, 
			boolean isTraining, boolean isTesting) {
		/*if (instance != null) {
			this.instance = new SerializableInstance(instance);
		}*/
		this.instanceIndex = index;
		this.isTraining = isTraining;
		this.isTesting = isTesting;
	}
        
        public InstancesContentEvent(InstanceContentEvent event){
            this.instanceIndex = event.getInstanceIndex();
            this.isTraining = event.isTraining();
            this.isTesting = event.isTesting();
        }

        protected List<Instance> instanceList = new LinkedList<Instance>();
        
        public void add(Instance instance){
            instanceList.add(new SerializableInstance(instance));
        }
        
	/**
	 * Gets the single instance of InstanceEvent.
	 * 
	 * @return the instance.
	 */
	public Instance[] getInstances() {
		return instanceList.toArray(new Instance[instanceList.size()]);
	}

	/**
	 * Gets the instance index.
	 *
	 * @return the index of the data vector.
	 */
	public long getInstanceIndex() {
		return instanceIndex;
	}

	/**
	 * Checks if is training.
	 *
	 * @return true if this is training data.
	 */
	public boolean isTraining() {
		return isTraining;
	}
	
	/**
	 * Checks if is testing.
	 *
	 * @return true if this is testing data.
	 */
	public boolean isTesting(){
		return isTesting;
	}

	/**
	 * Gets the classifier index.
	 *
	 * @return the classifier index
	 */
	public int getClassifierIndex() {
		return classifierIndex;
	}

	/**
	 * Sets the classifier index.
	 *
	 * @param classifierIndex the new classifier index
	 */
	public void setClassifierIndex(int classifierIndex) {
		this.classifierIndex = classifierIndex;
	}

	/**
	 * Gets the evaluation index.
	 *
	 * @return the evaluation index
	 */
	public int getEvaluationIndex() {
		return evaluationIndex;
	}

	/**
	 * Sets the evaluation index.
	 *
	 * @param evaluationIndex the new evaluation index
	 */
	public void setEvaluationIndex(int evaluationIndex) {
		this.evaluationIndex = evaluationIndex;
	}

	/* (non-Javadoc)
	 * @see samoa.core.ContentEvent#getKey(int)
	 */
	public String getKey(int key) {
		if (key == 0) 
			return Long.toString(this.getEvaluationIndex());
		else return Long.toString(10000
				* this.getEvaluationIndex()
				+ this.getClassifierIndex());
	}

	@Override
	public String getKey() {
		//System.out.println("InstanceContentEvent "+Long.toString(this.instanceIndex));
		return Long.toString(this.getClassifierIndex());
	}

	@Override
	public void setKey(String str) {
		this.instanceIndex = Long.parseLong(str);
	}

	@Override
	public boolean isLastEvent() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	
	
}
