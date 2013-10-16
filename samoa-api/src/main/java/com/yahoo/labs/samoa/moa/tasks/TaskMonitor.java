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
 * Interface representing a task monitor. 
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $ 
 */
public interface TaskMonitor {

    /**
     * Sets the description and the percentage done of the current activity.
     *
     * @param activity the description of the current activity
     * @param fracComplete the percentage done of the current activity
     */
    public void setCurrentActivity(String activityDescription,
            double fracComplete);

    /**
     * Sets the description of the current activity.
     *
     * @param activity the description of the current activity
     */
    public void setCurrentActivityDescription(String activity);

    /**
     * Sets the percentage done of the current activity
     *
     * @param fracComplete the percentage done of the current activity
     */
    public void setCurrentActivityFractionComplete(double fracComplete);

    /**
     * Gets whether the task should abort.
     *
     * @return true if the task should abort
     */
    public boolean taskShouldAbort();

    /**
     * Gets whether there is a request for preview the task result.
     *
     * @return true if there is a request for preview the task result
     */
    public boolean resultPreviewRequested();

    /**
     * Sets the current result to preview
     *
     * @param latestPreview the result to preview
     */
    public void setLatestResultPreview(Object latestPreview);

    /**
     * Gets the description of the current activity.
     *
     * @return the description of the current activity
     */
    public String getCurrentActivityDescription();

    /**
     * Gets the percentage done of the current activity
     *
     * @return the percentage done of the current activity
     */
    public double getCurrentActivityFractionComplete();

    /**
     * Requests the task monitored to pause.
     *
     */
    public void requestPause();

    /**
     * Requests the task monitored to resume.
     *
     */
    public void requestResume();

    /**
     * Requests the task monitored to cancel.
     *
     */
    public void requestCancel();

    /**
     * Gets whether the task monitored is paused.
     *
     * @return true if the task is paused
     */
    public boolean isPaused();

    /**
     * Gets whether the task monitored is cancelled.
     *
     * @return true if the task is cancelled
     */
    public boolean isCancelled();

    /**
     * Requests to preview the task result.
     *
     */
    public void requestResultPreview();

    /**
     * Requests to preview the task result.
     *
     * @param toInform the listener of the changes in the preview of the result
     */
    public void requestResultPreview(ResultPreviewListener toInform);

    /**
     * Gets the current result to preview
     *
     * @return the result to preview
     */
    public Object getLatestResultPreview();
}
