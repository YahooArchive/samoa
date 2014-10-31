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

import com.yahoo.labs.samoa.LocalStormDoTask;
import com.yahoo.labs.samoa.TestParams;
import com.yahoo.labs.samoa.TestUtils;
import org.junit.Test;

public class AlgosTest {


    @Test(timeout = 60000)
    public void testVHTWithStorm() throws Exception {

        TestParams vhtConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(200_000)
                .setClassifiedInstances(200_000)
                .setClassificationsCorrect(55f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_VHT_RANDOMTREE)
                .setResultFilePollTimeout(30)
                .setPrePollWait(15)
                .setTaskClassName(LocalStormDoTask.class.getName())
                .build();
        TestUtils.test(vhtConfig);

    }

    @Test(timeout = 120000)
    public void testBaggingWithStorm() throws Exception {
        TestParams baggingConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(180_000)
                .setClassifiedInstances(190_000)
                .setClassificationsCorrect(64f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_BAGGING_RANDOMTREE)
                .setResultFilePollTimeout(40)
                .setPrePollWait(20)
                .setTaskClassName(LocalStormDoTask.class.getName())
                .build();
        TestUtils.test(baggingConfig);

    }


}
