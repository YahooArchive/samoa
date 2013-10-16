package com.yahoo.labs.samoa.moa.tasks;

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

/**
 * Interface implemented by classes that preview results 
 * on the Graphical User Interface 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public interface ResultPreviewListener {

    /**
     * This method is used to receive a signal from
     * <code>TaskMonitor</code> that the lastest preview has
     * changed. This method is implemented in <code>PreviewPanel</code>
     * to change the results that are shown in its panel.
     *
     */
    public void latestPreviewChanged();
}
