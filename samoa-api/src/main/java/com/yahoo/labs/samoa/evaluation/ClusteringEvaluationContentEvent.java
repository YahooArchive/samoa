package com.yahoo.labs.samoa.evaluation;

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
import com.yahoo.labs.samoa.core.*;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.moa.cluster.Clustering;
import com.yahoo.labs.samoa.moa.core.DataPoint;

/**
 * License
 */
/**
 * The Class Clustering ResultEvent.
 */
final public class ClusteringEvaluationContentEvent implements ContentEvent {

    private static final long serialVersionUID = -7746983521296618922L;
    private Clustering gtClustering;
    private DataPoint dataPoint;
    private final boolean isLast;
    private String key = "0";

    public ClusteringEvaluationContentEvent() {
        this.isLast = false;
    }

    public ClusteringEvaluationContentEvent(boolean isLast) {
        this.isLast = isLast;
    }

    /**
     * Instantiates a new gtClustering result event.
     *
     * @param clustering the gtClustering result
     * @param instance data point
     * @param isLast is the last result
     */
    public ClusteringEvaluationContentEvent(Clustering clustering, DataPoint instance, boolean isLast) {
        this.gtClustering = clustering;
        this.isLast = isLast;
        this.dataPoint = instance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isLastEvent() {
        return this.isLast;
    }

    Clustering getGTClustering() {
        return this.gtClustering;
    }
    
    DataPoint getDataPoint() {
        return this.dataPoint;
    }
    
}
