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

import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Instance;

/**
 * Nominal binary conditional test for instances to use to split nodes in Hoeffding trees.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class NominalAttributeBinaryTest extends InstanceConditionalBinaryTest {

    private static final long serialVersionUID = 1L;

    protected int attIndex;

    protected int attValue;

    public NominalAttributeBinaryTest(int attIndex, int attValue) {
        this.attIndex = attIndex;
        this.attValue = attValue;
    }

    @Override
    public int branchForInstance(Instance inst) {
        int instAttIndex = this.attIndex < inst.classIndex() ? this.attIndex
                : this.attIndex + 1;
        return inst.isMissing(instAttIndex) ? -1 : ((int) inst.value(instAttIndex) == this.attValue ? 0 : 1);
    }

    @Override
    public String describeConditionForBranch(int branch, InstancesHeader context) {
        if ((branch == 0) || (branch == 1)) {
            return InstancesHeader.getAttributeNameString(context,
                    this.attIndex)
                    + (branch == 0 ? " = " : " != ")
                    + InstancesHeader.getNominalValueString(context,
                    this.attIndex, this.attValue);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    public int[] getAttsTestDependsOn() {
        return new int[]{this.attIndex};
    }
}
