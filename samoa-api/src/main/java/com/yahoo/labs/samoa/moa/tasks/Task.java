package com.yahoo.labs.samoa.moa.tasks;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
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

import com.yahoo.labs.samoa.moa.MOAObject;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;

/**
 * Interface representing a task. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface Task extends MOAObject {

    /**
     * Gets the result type of this task.
     * Tasks can return LearningCurve, LearningEvaluation,
     * Classifier, String, Instances..
     *
     * @return a class object of the result of this task
     */
    public Class<?> getTaskResultType();

    /**
     * This method performs this task,
     * when TaskMonitor and ObjectRepository are no needed.
     *
     * @return an object with the result of this task
     */
    public Object doTask();

    /**
     * This method performs this task.
     * <code>AbstractTask</code> implements this method so all
     * its extensions only need to implement <code>doTaskImpl</code>
     *
     * @param monitor the TaskMonitor to use
     * @param repository  the ObjectRepository to use
     * @return an object with the result of this task
     */
    public Object doTask(TaskMonitor monitor, ObjectRepository repository);
}
