package com.yahoo.labs.samoa.moa.streams;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.moa.core.InputStreamProgressMonitor;
import com.yahoo.labs.samoa.moa.core.InstanceExample;
import com.yahoo.labs.samoa.moa.core.ObjectRepository;
import com.yahoo.labs.samoa.moa.options.AbstractOptionHandler;
import com.yahoo.labs.samoa.moa.tasks.TaskMonitor;

/**
 * Stream reader of ARFF files.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class ArffFileStream extends AbstractOptionHandler implements InstanceStream {

    @Override
    public String getPurposeString() {
        return "A stream read from an ARFF file.";
    }

    private static final long serialVersionUID = 1L;

    public FileOption arffFileOption = new FileOption("arffFile", 'f',
            "ARFF file to load.", null, "arff", false);

    public IntOption classIndexOption = new IntOption(
            "classIndex",
            'c',
            "Class index of data. 0 for none or -1 for last attribute in file.",
            -1, -1, Integer.MAX_VALUE);

    protected Instances instances;

    transient protected Reader fileReader;

    protected boolean hitEndOfFile;

    protected InstanceExample lastInstanceRead;

    protected int numInstancesRead;

    transient protected InputStreamProgressMonitor fileProgressMonitor;
    
    protected boolean hasStarted;

    public ArffFileStream() {
    }

    public ArffFileStream(String arffFileName, int classIndex) {
        this.arffFileOption.setValue(arffFileName);
        this.classIndexOption.setValue(classIndex);
        this.hasStarted = false;
        restart();
    }

    @Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        //restart();
        this.hasStarted = false;
        this.lastInstanceRead = null;
    }

    @Override
    public InstancesHeader getHeader() {
        return new InstancesHeader(this.instances);
    }

    @Override
    public long estimatedRemainingInstances() {
        double progressFraction = this.fileProgressMonitor.getProgressFraction();
        if ((progressFraction > 0.0) && (this.numInstancesRead > 0)) {
            return (long) ((this.numInstancesRead / progressFraction) - this.numInstancesRead);
        }
        return -1;
    }

    @Override
    public boolean hasMoreInstances() {
        return !this.hitEndOfFile;
    }

    @Override
    public InstanceExample nextInstance() {
        if (this.lastInstanceRead == null) {
            readNextInstanceFromFile();
        }
        InstanceExample prevInstance = this.lastInstanceRead;
        this.hitEndOfFile = !readNextInstanceFromFile();
        return prevInstance;
    }

    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public void restart() {
        try {
            reset();
            //this.hitEndOfFile = !readNextInstanceFromFile();
        } catch (IOException ioe) {
            throw new RuntimeException("ArffFileStream restart failed.", ioe);
        }
    }

    protected boolean readNextInstanceFromFile() {
        boolean ret;
        if (!this.hasStarted){
            try {
                reset();
                ret = getNextInstanceFromFile();
                this.hitEndOfFile = !ret;
            } catch (IOException ioe) {
                throw new RuntimeException("ArffFileStream restart failed.", ioe);
            }
            this.hasStarted = true;
        } else {
            ret = getNextInstanceFromFile();
        }
        return ret;
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    private void reset() throws IOException {
        if (this.fileReader != null) {
            this.fileReader.close();
        }
        InputStream fileStream = new FileInputStream(this.arffFileOption.getFile());
        this.fileProgressMonitor = new InputStreamProgressMonitor(
                fileStream);
        this.fileReader = new BufferedReader(new InputStreamReader(
                this.fileProgressMonitor));
        this.instances = new Instances(this.fileReader, 1, this.classIndexOption.getValue());
        if (this.classIndexOption.getValue() < 0) {
            this.instances.setClassIndex(this.instances.numAttributes() - 1);
        } else if (this.classIndexOption.getValue() > 0) {
            this.instances.setClassIndex(this.classIndexOption.getValue() - 1);
        }
        this.numInstancesRead = 0;
        this.lastInstanceRead = null;
    }

    private boolean getNextInstanceFromFile() throws RuntimeException {
        try {
            if (this.instances.readInstance(this.fileReader)) {
                this.lastInstanceRead = new InstanceExample(this.instances.instance(0));
                this.instances.delete(); // keep instances clean
                this.numInstancesRead++;
                return true;
            }
            if (this.fileReader != null) {
                this.fileReader.close();
                this.fileReader = null;
            }
            return false;
        } catch (IOException ioe) {
            throw new RuntimeException(
                    "ArffFileStream failed to read instance from stream.", ioe);
        }
    }
}
