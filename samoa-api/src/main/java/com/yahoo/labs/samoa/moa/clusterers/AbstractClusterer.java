package com.yahoo.labs.samoa.moa.clusterers;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import com.yahoo.labs.samoa.moa.cluster.Clustering;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.moa.core.Measurement;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.core.StringUtils;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.github.javacliparser.FlagOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

public abstract class AbstractClusterer extends AbstractOptionHandler
		implements Clusterer {
	
	@Override
	public String getPurposeString() {
		return "MOA Clusterer: " + getClass().getCanonicalName();
	}

	protected InstancesHeader modelContext;

	protected double trainingWeightSeenByModel = 0.0;

	protected int randomSeed = 1;

	protected IntOption randomSeedOption;

    public FlagOption evaluateMicroClusteringOption;

    protected Random clustererRandom;

    protected Clustering clustering;
    
	public AbstractClusterer() {
		if (isRandomizable()) {
			this.randomSeedOption = new IntOption("randomSeed", 'r',
					"Seed for random behaviour of the Clusterer.", 1);
		}

        if( implementsMicroClusterer()){
            this.evaluateMicroClusteringOption =
                    new FlagOption("evaluateMicroClustering", 'M',
                    "Evaluate the underlying microclustering instead of the macro clustering");
        }
	}

	@Override
	public void prepareForUseImpl(TaskMonitor monitor,
			ObjectRepository repository) {
		if (this.randomSeedOption != null) {
			this.randomSeed = this.randomSeedOption.getValue();
		}
		if (!trainingHasStarted()) {
			resetLearning();
		}
                clustering = new Clustering();
	}

	public void setModelContext(InstancesHeader ih) {
		if ((ih != null) && (ih.classIndex() < 0)) {
			throw new IllegalArgumentException(
					"Context for a Clusterer must include a class to learn");
		}
		if (trainingHasStarted()
				&& (this.modelContext != null)
				&& ((ih == null) || !contextIsCompatible(this.modelContext, ih))) {
			throw new IllegalArgumentException(
					"New context is not compatible with existing model");
		}
		this.modelContext = ih;
	}

	public InstancesHeader getModelContext() {
		return this.modelContext;
	}

	public void setRandomSeed(int s) {
		this.randomSeed = s;
		if (this.randomSeedOption != null) {
			// keep option consistent
			this.randomSeedOption.setValue(s);
		}
	}

	public boolean trainingHasStarted() {
		return this.trainingWeightSeenByModel > 0.0;
	}

	public double trainingWeightSeenByModel() {
		return this.trainingWeightSeenByModel;
	}

	public void resetLearning() {
		this.trainingWeightSeenByModel = 0.0;
		if (isRandomizable()) {
			this.clustererRandom = new Random(this.randomSeed);
		}
		resetLearningImpl();
	}

	public void trainOnInstance(Instance inst) {
		if (inst.weight() > 0.0) {
			this.trainingWeightSeenByModel += inst.weight();
			trainOnInstanceImpl(inst);
		}
	}

	public Measurement[] getModelMeasurements() {
		List<Measurement> measurementList = new LinkedList<Measurement>();
		measurementList.add(new Measurement("model training instances",
				trainingWeightSeenByModel()));
		measurementList.add(new Measurement("model serialized size (bytes)",
				measureByteSize()));
		Measurement[] modelMeasurements = getModelMeasurementsImpl();
		if (modelMeasurements != null) {
			for (Measurement measurement : modelMeasurements) {
				measurementList.add(measurement);
			}
		}
		// add average of sub-model measurements
		Clusterer[] subModels = getSubClusterers();
		if ((subModels != null) && (subModels.length > 0)) {
			List<Measurement[]> subMeasurements = new LinkedList<Measurement[]>();
			for (Clusterer subModel : subModels) {
				if (subModel != null) {
					subMeasurements.add(subModel.getModelMeasurements());
				}
			}
			Measurement[] avgMeasurements = Measurement
					.averageMeasurements(subMeasurements
							.toArray(new Measurement[subMeasurements.size()][]));
			for (Measurement measurement : avgMeasurements) {
				measurementList.add(measurement);
			}
		}
		return measurementList.toArray(new Measurement[measurementList.size()]);
	}

	public void getDescription(StringBuilder out, int indent) {
		StringUtils.appendIndented(out, indent, "Model type: ");
		out.append(this.getClass().getName());
		StringUtils.appendNewline(out);
		Measurement.getMeasurementsDescription(getModelMeasurements(), out,
				indent);
		StringUtils.appendNewlineIndented(out, indent, "Model description:");
		StringUtils.appendNewline(out);
		if (trainingHasStarted()) {
			getModelDescription(out, indent);
		} else {
			StringUtils.appendIndented(out, indent,
					"Model has not been trained.");
		}
	}

	public Clusterer[] getSubClusterers() {
		return null;
	}

	@Override
	public Clusterer copy() {
		return (Clusterer) super.copy();
	}

//	public boolean correctlyClassifies(Instance inst) {
//		return Utils.maxIndex(getVotesForInstance(inst)) == (int) inst
//				.classValue();
//	}

	public String getClassNameString() {
		return InstancesHeader.getClassNameString(this.modelContext);
	}

	public String getClassLabelString(int classLabelIndex) {
		return InstancesHeader.getClassLabelString(this.modelContext,
				classLabelIndex);
	}

	public String getAttributeNameString(int attIndex) {
		return InstancesHeader.getAttributeNameString(this.modelContext,
				attIndex);
	}

	public String getNominalValueString(int attIndex, int valIndex) {
		return InstancesHeader.getNominalValueString(this.modelContext,
				attIndex, valIndex);
	}

	// originalContext notnull
	// newContext notnull
	public static boolean contextIsCompatible(InstancesHeader originalContext,
			InstancesHeader newContext) {
		// rule 1: num classes can increase but never decrease
		// rule 2: num attributes can increase but never decrease
		// rule 3: num nominal attribute values can increase but never decrease
		// rule 4: attribute types must stay in the same order (although class
		// can
		// move; is always skipped over)
		// attribute names are free to change, but should always still represent
		// the original attributes
		if (newContext.numClasses() < originalContext.numClasses()) {
			return false; // rule 1
		}
		if (newContext.numAttributes() < originalContext.numAttributes()) {
			return false; // rule 2
		}
		int oPos = 0;
		int nPos = 0;
		while (oPos < originalContext.numAttributes()) {
			if (oPos == originalContext.classIndex()) {
				oPos++;
				if (!(oPos < originalContext.numAttributes())) {
					break;
				}
			}
			if (nPos == newContext.classIndex()) {
				nPos++;
			}
			if (originalContext.attribute(oPos).isNominal()) {
				if (!newContext.attribute(nPos).isNominal()) {
					return false; // rule 4
				}
				if (newContext.attribute(nPos).numValues() < originalContext
						.attribute(oPos).numValues()) {
					return false; // rule 3
				}
			} else {
				assert (originalContext.attribute(oPos).isNumeric());
				if (!newContext.attribute(nPos).isNumeric()) {
					return false; // rule 4
				}
			}
			oPos++;
			nPos++;
		}
		return true; // all checks clear
	}

	// reason for ...Impl methods:
	// ease programmer burden by not requiring them to remember calls to super
	// in overridden methods & will produce compiler errors if not overridden

	public abstract void resetLearningImpl();

	public abstract void trainOnInstanceImpl(Instance inst);

	protected abstract Measurement[] getModelMeasurementsImpl();

	public abstract void getModelDescription(StringBuilder out, int indent);

	protected static int modelAttIndexToInstanceAttIndex(int index,
			Instance inst) {
		return inst.classIndex() > index ? index : index + 1;
	}

	protected static int modelAttIndexToInstanceAttIndex(int index,
			Instances insts) {
		return insts.classIndex() > index ? index : index + 1;
	}

        public boolean  implementsMicroClusterer(){
            return false;
        }

        public boolean  keepClassLabel(){
            return false;
        }
        
        public Clustering getMicroClusteringResult(){
            return null;
        };
}
