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

import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.moa.cluster.Clustering;

/**
 * License
 */
/**
 * The Class Clustering ResultEvent.
 */
final public class ClusteringResultContentEvent implements ContentEvent {

    private static final long serialVersionUID = -7746983521296618922L;
    private Clustering clustering;
    private final boolean isLast;
    private String key = "0";

    public ClusteringResultContentEvent() {
        this.isLast = false;
    }

    public ClusteringResultContentEvent(boolean isLast) {
        this.isLast = isLast;
    }

    /**
     * Instantiates a new clustering result event.
     *
     * @param clustering the clustering result
     * @param isLast is the last result
     */
    public ClusteringResultContentEvent(Clustering clustering, boolean isLast) {
        this.clustering = clustering;
        this.isLast = isLast;
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

    public Clustering getClustering() {
        return this.clustering;
    }
}
