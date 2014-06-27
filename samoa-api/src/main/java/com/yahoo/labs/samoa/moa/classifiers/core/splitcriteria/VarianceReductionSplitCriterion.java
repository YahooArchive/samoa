package com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 - 2014 Yahoo! Inc.
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
/*
 *    VarianceReductionSplitCriterion.java
 *    Copyright (C) 2013 University of Porto, Portugal
 *    @author Katie de Lange, E. Almeida, J. Gama
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 *    
 */

/* Project Knowledge Discovery from Data Streams, FCT LIAAD-INESC TEC, 
 *
 * Contact: jgama@fep.up.pt
 */

import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

public class VarianceReductionSplitCriterion extends AbstractOptionHandler implements SplitCriterion {

    private static final long serialVersionUID = 1L;

/*    @Override
    public double getMeritOfSplit(double[] preSplitDist, double[][] postSplitDists) {
   
    	double N = preSplitDist[0];
    	double SDR = computeSD(preSplitDist);

    //	System.out.print("postSplitDists.length"+postSplitDists.length+"\n");
    	for(int i = 0; i < postSplitDists.length; i++)
    	{
    		double Ni = postSplitDists[i][0];
    		SDR -= (Ni/N)*computeSD(postSplitDists[i]);
    	}

        return SDR;
    }*/
    
    @Override
    public double getMeritOfSplit(double[] preSplitDist, double[][] postSplitDists) {
        double SDR=0.0;
    	double N = preSplitDist[0];
    	int count = 0; 
    	
    	for(int i = 0; i < postSplitDists.length; i++)
    	{
    		double Ni = postSplitDists[i][0];
    		if(Ni >=5.0){
    			count = count +1;
    		}
    	}
    	
    	if(count == postSplitDists.length){
    		SDR = computeSD(preSplitDist);
    		for(int i = 0; i < postSplitDists.length; i++)
        	{
        		double Ni = postSplitDists[i][0];
        		SDR -= (Ni/N)*computeSD(postSplitDists[i]);
        	}
    	}
    	return SDR;
    }
    	


    @Override
    public double getRangeOfMerit(double[] preSplitDist) {
        return 1;
    }

    public static double computeSD(double[] dist) {
       
    	int N = (int)dist[0];
        double sum = dist[1];
        double sumSq = dist[2];
     //   return Math.sqrt((sumSq - ((sum * sum)/N))/N);
        return (sumSq - ((sum * sum)/N))/N;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        // TODO Auto-generated method stub
    }
    
}
