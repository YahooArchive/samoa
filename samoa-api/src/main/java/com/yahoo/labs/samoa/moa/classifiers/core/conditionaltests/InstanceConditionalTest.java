package com.yahoo.labs.samoa.moa.classifiers.core.conditionaltests;

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

import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Instance;

/**
 * Abstract conditional test for instances to use to split nodes in Hoeffding trees.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public abstract class InstanceConditionalTest extends AbstractMOAObject {

    /**
     *  Returns the number of the branch for an instance, -1 if unknown.
     *
     * @param inst the instance to be used
     * @return the number of the branch for an instance, -1 if unknown.
     */
    public abstract int branchForInstance(Instance inst);

    /**
     * Gets whether the number of the branch for an instance is known.
     *
     * @param inst
     * @return true if the number of the branch for an instance is known
     */
    public boolean resultKnownForInstance(Instance inst) {
        return branchForInstance(inst) >= 0;
    }

    /**
     * Gets the number of maximum branches, -1 if unknown.
     *
     * @return the number of maximum branches, -1 if unknown..
     */
    public abstract int maxBranches();

    /**
     * Gets the text that describes the condition of a branch. It is used to describe the branch.
     *
     * @param branch the number of the branch to describe
     * @param context the context or header of the data stream
     * @return the text that describes the condition of the branch
     */
    public abstract String describeConditionForBranch(int branch,
            InstancesHeader context);

    /**
     * Returns an array with the attributes that the test depends on.
     *
     * @return  an array with the attributes that the test depends on
     */
    public abstract int[] getAttsTestDependsOn();
}
