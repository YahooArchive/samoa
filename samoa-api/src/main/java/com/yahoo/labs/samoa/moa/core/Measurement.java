package com.yahoo.labs.samoa.moa.core;

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

/**
 * Class for storing an evaluation measurement.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class Measurement extends AbstractMOAObject {

    private static final long serialVersionUID = 1L;

    protected String name;

    protected double value;

    public Measurement(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public double getValue() {
        return this.value;
    }

    public static Measurement getMeasurementNamed(String name,
            Measurement[] measurements) {
        for (Measurement measurement : measurements) {
            if (name.equals(measurement.getName())) {
                return measurement;
            }
        }
        return null;
    }

    public static void getMeasurementsDescription(Measurement[] measurements,
            StringBuilder out, int indent) {
        if (measurements.length > 0) {
            StringUtils.appendIndented(out, indent, measurements[0].toString());
            for (int i = 1; i < measurements.length; i++) {
                StringUtils.appendNewlineIndented(out, indent, measurements[i].toString());
            }

        }
    }

    public static Measurement[] averageMeasurements(Measurement[][] toAverage) {
        List<String> measurementNames = new ArrayList<String>();
        for (Measurement[] measurements : toAverage) {
            for (Measurement measurement : measurements) {
                if (measurementNames.indexOf(measurement.getName()) < 0) {
                    measurementNames.add(measurement.getName());
                }
            }
        }
        GaussianEstimator[] estimators = new GaussianEstimator[measurementNames.size()];
        for (int i = 0; i < estimators.length; i++) {
            estimators[i] = new GaussianEstimator();
        }
        for (Measurement[] measurements : toAverage) {
            for (Measurement measurement : measurements) {
                estimators[measurementNames.indexOf(measurement.getName())].addObservation(measurement.getValue(), 1.0);
            }
        }
        List<Measurement> averagedMeasurements = new ArrayList<Measurement>();
        for (int i = 0; i < measurementNames.size(); i++) {
            String mName = measurementNames.get(i);
            GaussianEstimator mEstimator = estimators[i];
            if (mEstimator.getTotalWeightObserved() > 1.0) {
                averagedMeasurements.add(new Measurement("[avg] " + mName,
                        mEstimator.getMean()));
                averagedMeasurements.add(new Measurement("[err] " + mName,
                        mEstimator.getStdDev()
                        / Math.sqrt(mEstimator.getTotalWeightObserved())));
            }
        }
        return averagedMeasurements.toArray(new Measurement[averagedMeasurements.size()]);
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append(getName());
        sb.append(" = ");
        sb.append(StringUtils.doubleToString(getValue(), 3));
    }
}
