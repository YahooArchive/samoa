package com.yahoo.labs.samoa.moa.options;

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

import com.github.javacliparser.Configurable;
import com.github.javacliparser.Options;
import com.yahoo.labs.samoa.moa.MOAObject;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Interface representing an object that handles options or parameters. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface OptionHandler extends MOAObject, Configurable {

    /**
     * Gets the purpose of this object
     *
     * @return the string with the purpose of this object
     */
    public String getPurposeString();

    /**
     * Gets the options of this object
     *
     * @return the options of this object
     */
    public Options getOptions();

    /**
     * This method prepares this object for use.
     *
     */
    public void prepareForUse();

    /**
     * This method prepares this object for use.
     *
     * @param monitor the TaskMonitor to use
     * @param repository  the ObjectRepository to use
     */
    public void prepareForUse(TaskMonitor monitor, ObjectRepository repository);

    /**
     * This method produces a copy of this object.
     *
     * @return a copy of this object
     */
    @Override
    public OptionHandler copy();

    /**
     * Gets the Command Line Interface text to create the object
     *
     * @return the Command Line Interface text to create the object
     */
    public String getCLICreationString(Class<?> expectedType);
}
