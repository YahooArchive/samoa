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
 * Class that represents a null monitor.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class NullMonitor implements TaskMonitor {

    @Override
    public void setCurrentActivity(String activityDescription,
            double fracComplete) {
    }

    @Override
    public void setCurrentActivityDescription(String activity) {
    }

    @Override
    public void setCurrentActivityFractionComplete(double fracComplete) {
    }

    @Override
    public boolean taskShouldAbort() {
        return false;
    }

    @Override
    public String getCurrentActivityDescription() {
        return null;
    }

    @Override
    public double getCurrentActivityFractionComplete() {
        return -1.0;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void requestCancel() {
    }

    @Override
    public void requestPause() {
    }

    @Override
    public void requestResume() {
    }

    @Override
    public Object getLatestResultPreview() {
        return null;
    }

    @Override
    public void requestResultPreview() {
    }

    @Override
    public boolean resultPreviewRequested() {
        return false;
    }

    @Override
    public void setLatestResultPreview(Object latestPreview) {
    }

    @Override
    public void requestResultPreview(ResultPreviewListener toInform) {
    }
}
