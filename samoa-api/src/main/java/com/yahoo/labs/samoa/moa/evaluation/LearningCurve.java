package com.yahoo.labs.samoa.moa.evaluation;

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

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.moa.AbstractMOAObject;
import com.yahoo.labs.samoa.moa.core.DoubleVector;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.moa.core.StringUtils;

/**
 * Class that stores and keeps the history of evaluation measurements.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class LearningCurve extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected List<String> measurementNames = new ArrayList<String>();

    protected List<double[]> measurementValues = new ArrayList<double[]>();

    public LearningCurve(String orderingMeasurementName) {
        this.measurementNames.add(orderingMeasurementName);
    }

    public String getOrderingMeasurementName() {
        return this.measurementNames.get(0);
    }

    public void insertEntry(LearningEvaluation learningEvaluation) {
        Measurement[] measurements = learningEvaluation.getMeasurements();
        Measurement orderMeasurement = Measurement.getMeasurementNamed(
                getOrderingMeasurementName(), measurements);
        if (orderMeasurement == null) {
            throw new IllegalArgumentException();
        }
        DoubleVector entryVals = new DoubleVector();
        for (Measurement measurement : measurements) {
            entryVals.setValue(addMeasurementName(measurement.getName()),
                    measurement.getValue());
        }
        double orderVal = orderMeasurement.getValue();
        int index = 0;
        while ((index < this.measurementValues.size())
                && (orderVal > this.measurementValues.get(index)[0])) {
            index++;
        }
        this.measurementValues.add(index, entryVals.getArrayRef());
    }

    public int numEntries() {
        return this.measurementValues.size();
    }

    protected int addMeasurementName(String name) {
        int index = this.measurementNames.indexOf(name);
        if (index < 0) {
            index = this.measurementNames.size();
            this.measurementNames.add(name);
        }
        return index;
    }

    public String headerToString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String name : this.measurementNames) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(name);
        }
        return sb.toString();
    }

    public String entryToString(int entryIndex) {
        StringBuilder sb = new StringBuilder();
        double[] vals = this.measurementValues.get(entryIndex);
        for (int i = 0; i < this.measurementNames.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            if ((i >= vals.length) || Double.isNaN(vals[i])) {
                sb.append('?');
            } else {
                sb.append(Double.toString(vals[i]));
            }
        }
        return sb.toString();
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append(headerToString());
        for (int i = 0; i < numEntries(); i++) {
            StringUtils.appendNewlineIndented(sb, indent, entryToString(i));
        }
    }

    public double getMeasurement(int entryIndex, int measurementIndex) {
        return this.measurementValues.get(entryIndex)[measurementIndex];
    }

    public String getMeasurementName(int measurementIndex) {
        return this.measurementNames.get(measurementIndex);
    }
}
