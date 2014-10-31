package com.yahoo.labs.samoa;

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

import org.junit.Test;

public class AlgosTest {


    @Test
    public void testVHTLocal() throws Exception {

        TestParams vhtConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(200_000)
                .setClassifiedInstances(200_000)
                .setClassificationsCorrect(75f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_VHT_RANDOMTREE)
                .setResultFilePollTimeout(10)
                .setPrePollWait(10)
                .setTaskClassName(LocalDoTask.class.getName())
                .build();
        TestUtils.test(vhtConfig);

    }

    @Test
    public void testBaggingLocal() throws Exception {
        TestParams baggingConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(180_000)
                .setClassifiedInstances(210_000)
                .setClassificationsCorrect(60f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_BAGGING_RANDOMTREE)
                .setPrePollWait(10)
                .setResultFilePollTimeout(10)
                .setTaskClassName(LocalDoTask.class.getName())
                .build();
        TestUtils.test(baggingConfig);

    }

    @Test
    public void testNaiveBayesLocal() throws Exception {

        TestParams vhtConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(200_000)
                .setClassifiedInstances(200_000)
                .setClassificationsCorrect(65f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_NAIVEBAYES_HYPERPLANE)
                .setResultFilePollTimeout(10)
                .setPrePollWait(10)
                .setTaskClassName(LocalDoTask.class.getName())
                .build();
        TestUtils.test(vhtConfig);

    }

}
