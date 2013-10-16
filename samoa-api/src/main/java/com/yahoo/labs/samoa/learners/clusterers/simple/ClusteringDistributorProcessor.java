package com.yahoo.labs.samoa.learners.clusterers.simple;

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
/**
 * License
 */
import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.core.Processor;
import com.yahoo.labs.samoa.learners.clusterers.ClusteringContentEvent;
import com.yahoo.labs.samoa.topology.Stream;

/**
 * The Class ClusteringDistributorPE.
 */
public class ClusteringDistributorProcessor implements Processor {

    private static final long serialVersionUID = -1550901409625192730L;
    /**
     * The output stream.
     */
    private Stream outputStream;

    public Stream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(Stream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Process event.
     *
     * @param event the event
     * @return true, if successful
     */
    public boolean process(ContentEvent event) {
        ClusteringContentEvent inEvent = (ClusteringContentEvent) event;
        outputStream.put(event);
        return true;
    }

    /* (non-Javadoc)
     * @see samoa.core.Processor#newProcessor(samoa.core.Processor)
     */
    @Override
    public Processor newProcessor(Processor sourceProcessor) {
        ClusteringDistributorProcessor newProcessor = new ClusteringDistributorProcessor();
        ClusteringDistributorProcessor originProcessor = (ClusteringDistributorProcessor) sourceProcessor;
        if (originProcessor.getOutputStream() != null) {
            newProcessor.setOutputStream(originProcessor.getOutputStream());
        }

        return newProcessor;
    }

    public void onCreate(int id) {
    }
}
