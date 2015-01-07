package com.yahoo.labs.samoa.evaluation.measures;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2010 RWTH Aachen University, Germany
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

import com.yahoo.labs.samoa.moa.cluster.Clustering;
import com.yahoo.labs.samoa.moa.core.DataPoint;
import com.yahoo.labs.samoa.moa.evaluation.MeasureCollection;
import com.yahoo.labs.samoa.instances.Instance;

public class SSQ extends MeasureCollection {

    public SSQ() {
        super();
    }

    @Override
    public String[] getNames() {
        return new String[]{"SSQ"};
    }

    @Override
    protected boolean[] getDefaultEnabled() {
        return new boolean[]{false};
    }

    // TODO Work on this later
    //@Override
    public void evaluateClusteringSamoa(Clustering clustering,
            Clustering trueClsutering, ArrayList<Instance> points) {
        double sum = 0.0;
        for (Instance point : points) {
            // don't include noise
            if (point.classValue() == -1) {
                continue;
            }

            double minDistance = Double.MAX_VALUE;
            for (int c = 0; c < clustering.size(); c++) {
                double distance = 0.0;
                double[] center = clustering.get(c).getCenter();
                for (int i = 0; i < center.length; i++) {
                    double d = point.value(i) - center[i];
                    distance += d * d;
                }
                minDistance = Math.min(distance, minDistance);
            }

            sum += minDistance;
        }

        addValue(0, sum);
    }

    @Override
     public void evaluateClustering(Clustering clustering, Clustering trueClsutering, ArrayList<DataPoint> points) {
        double sum = 0.0;
        for (int p = 0; p < points.size(); p++) {
            //don't include noise
            if(points.get(p).classValue()==-1) continue;

            double minDistance = Double.MAX_VALUE;
            for (int c = 0; c < clustering.size(); c++) {
                double distance = 0.0;
                double[] center = clustering.get(c).getCenter();
                for (int i = 0; i < center.length; i++) {
                    double d = points.get(p).value(i) - center[i];
                    distance += d * d;
                }
                minDistance = Math.min(distance, minDistance);
            }
            
            sum+=minDistance;
        }
        
        addValue(0,sum);
    }
}
