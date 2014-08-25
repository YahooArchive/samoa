package com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
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

public class SDRSplitCriterion extends VarianceReductionSplitCriterion {
	private static final long serialVersionUID = 1L;

	public static double computeSD(double[] dist) {
    	int N = (int)dist[0];
        double sum = dist[1];
        double sumSq = dist[2];
        return Math.sqrt((sumSq - ((sum * sum)/N))/N);
    }

}
