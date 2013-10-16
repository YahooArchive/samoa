package com.yahoo.labs.samoa.learners.classifiers.trees;

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

import com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers.AttributeClassObserver;
import com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers.DiscreteAttributeClassObserver;
import com.yahoo.labs.samoa.moa.classifiers.core.attributeclassobservers.NumericAttributeClassObserver;
import com.yahoo.labs.samoa.moa.classifiers.core.splitcriteria.SplitCriterion;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.learners.Learner;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.Stream;
import com.yahoo.labs.samoa.topology.TopologyBuilder;

import com.github.javacliparser.ClassOption;
import com.github.javacliparser.Configurable;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;

/**
 * Vertical Hoeffding Tree. 
 * 
 * Vertical Hoeffding Tree (VHT) classifier is a distributed classifier that utilizes vertical parallelism 
 * on top of Very Fast Decision Tree (VFDT) classifier. 
 * 
 * @author Arinto Murdopo
 *
 */
public final class VerticalHoeffdingTree implements Learner, Configurable{
    
	
	private static final long serialVersionUID = -4937416312929984057L;
	
    public ClassOption numericEstimatorOption = new ClassOption("numericEstimator",
            'n', "Numeric estimator to use.", NumericAttributeClassObserver.class,
            "GaussianNumericAttributeClassObserver");

    public ClassOption nominalEstimatorOption = new ClassOption("nominalEstimator",
            'd', "Nominal estimator to use.", DiscreteAttributeClassObserver.class,
            "NominalAttributeClassObserver");
	
    public ClassOption splitCriterionOption = new ClassOption("splitCriterion",
            's', "Split criterion to use.", SplitCriterion.class,
            "InfoGainSplitCriterion");
    
    public FloatOption splitConfidenceOption = new FloatOption(
            "splitConfidence",
            'c',
            "The allowable error in split decision, values closer to 0 will take longer to decide.",
            0.0000001, 0.0, 1.0);

    public FloatOption tieThresholdOption = new FloatOption("tieThreshold",
            't', "Threshold below which a split will be forced to break ties.",
            0.05, 0.0, 1.0);
    
    public IntOption gracePeriodOption = new IntOption(
            "gracePeriod",
            'g',
            "The number of instances a leaf should observe between split attempts.",
            200, 0, Integer.MAX_VALUE);
    
    public IntOption parallelismHintOption = new IntOption(
            "parallelismHint",
            'p',
            "The number of local statistics PI to do distributed computation",
            1, 1, Integer.MAX_VALUE);
    
    public IntOption timeOutOption = new IntOption(
            "timeOut",
            'o',
            "The duration to wait all distributed computation results from local statistics PI",
            30, 1, Integer.MAX_VALUE);
    
    public FlagOption binarySplitsOption = new FlagOption("binarySplits", 'b',
            "Only allow binary splits.");
    
    //TODO: (possible)
    //1. memoryEstimatedOption => for estimating model sizes
    //2. binarySplitsOption => for getting the best split suggestion, must be set in LocalStatisticsProcessor
    //3. stopMemManagementOption => for enforcing tracker limit, no tracker limit enforcement atm
    //4. removePoorAttsOption => no poor attributes remove at the moment
    //5. noPrePruneOption => always add null split as an option now
    
    private ModelAggregatorProcessor modelAggrProc;
    private ProcessingItem modelAggrPi;
    
    private Stream resultStream;   
    private Stream attributeStream;
	private Stream controlStream;
	
	private LocalStatisticsProcessor locStatProc;
	private ProcessingItem locStatPi;
	
	private Stream computeStream;
	        
	@Override
	public void init(TopologyBuilder topologyBuilder, Instances dataset) {
		this.modelAggrProc 
			= new ModelAggregatorProcessor.Builder(dataset)
											.splitCriterion((SplitCriterion) this.splitCriterionOption.getValue())
											.splitConfidence(splitConfidenceOption.getValue())
											.tieThreshold(tieThresholdOption.getValue())
											.gracePeriod(gracePeriodOption.getValue())
											.parallelismHint(parallelismHintOption.getValue())
											.timeOut(timeOutOption.getValue())
											.build();
		
		this.modelAggrPi = topologyBuilder.createPi(modelAggrProc);
	//	this.modelAggrPi.setName("modelAggrPi");
		
		this.resultStream = topologyBuilder.createStream(modelAggrPi);
	//	this.resultStream.setStreamId("resultStream");
		this.modelAggrProc.setResultStream(resultStream);
		
		this.attributeStream = topologyBuilder.createStream(modelAggrPi);
	//	this.attributeStream.setStreamId("attributeStream");
		this.modelAggrProc.setAttributeStream(attributeStream);
		
		this.controlStream = topologyBuilder.createStream(modelAggrPi);
	//	this.controlStream.setStreamId("controlStream");
		this.modelAggrProc.setControlStream(controlStream);
		
		this.locStatProc 
			= new LocalStatisticsProcessor.Builder()
					.splitCriterion((SplitCriterion) this.splitCriterionOption.getValue())
					.binarySplit(binarySplitsOption.isSet())
					.nominalClassObserver((AttributeClassObserver) this.nominalEstimatorOption.getValue())
					.numericClassObserver((AttributeClassObserver) this.numericEstimatorOption.getValue())
					.build();
		
		this.locStatPi = topologyBuilder.createPi(locStatProc, parallelismHintOption.getValue());
	//	this.locStatPi.setName("locStatPi");
		this.locStatPi.connectInputKeyStream(this.attributeStream);
		this.locStatPi.connectInputAllStream(this.controlStream);
		
		this.computeStream = topologyBuilder.createStream(locStatPi);
	//	this.computeStream.setStreamId("computeStream");
		
		this.locStatProc.setComputationResultStream(computeStream);
		this.modelAggrPi.connectInputAllStream(computeStream);		
	}

	@Override
	public ProcessingItem getInputProcessingItem() {
		return modelAggrPi;
	}

	@Override
	public Stream getResultStream() {
		return resultStream;
	}	
	
	static class LearningNodeIdGenerator{
		
		//TODO: add code to warn user of when value reaches Long.MAX_VALUES
		private static long id = 0;
		
		static synchronized long generate(){
			return id++;
		}
		
		
	}
}
