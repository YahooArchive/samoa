package com.yahoo.labs.samoa.moa.cluster;

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

import com.dreizak.miniball.model.ArrayPointSet;
import com.dreizak.miniball.model.PointSet;
import java.util.ArrayList;
import java.util.List;

public class Miniball {

    private int dimension;
    private com.dreizak.miniball.highdim.Miniball mb;
    private PointStorage pointSet;

    public Miniball(int dimension) {
        this.dimension = dimension;
    }

    void clear() {
        this.pointSet = new PointStorage(this.dimension);
    }

    void check_in(double[] array) {
        this.pointSet.add(array);
    }

    double[] center() {
        return this.mb.center();
    }

    double radius() {
        return this.mb.radius();
    }

    void build() {
        this.mb = new com.dreizak.miniball.highdim.Miniball(this.pointSet);
    }

    public class PointStorage implements PointSet {

        protected int dimension;
        protected List<double[]> L;

        public PointStorage(int dimension) {
            this.dimension = dimension;
            this.L = new ArrayList<double[]>();
        }

        public void add(double[] array) {
            this.L.add(array);
        }

        public int size() {
            return L.size();
        }

        public int dimension() {
            return dimension;
        }

        public double coord(int point, int coordinate) {
            return L.get(point)[coordinate];
        }
    }
}
