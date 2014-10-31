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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.impl.SimpleLoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.slf4j.spi.LoggerFactoryBinder;

public class AlgosTest {

    @Test(timeout = 60000)
    public void testVHTWithThreads() throws Exception {

        TestParams vhtConfig = new TestParams.Builder()
                .setInputInstances(200_000)
                .setSamplingSize(20_000)
                .setEvaluationInstances(200_000)
                .setClassifiedInstances(200_000)
                .setClassificationsCorrect(55f)
                .setKappaStat(-0.1f)
                .setKappaTempStat(-0.1f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_VHT_RANDOMTREE + " -t 2")
                .setResultFilePollTimeout(10)
                .setPrePollWait(10)
                .setTaskClassName(LocalThreadsDoTask.class.getName())
                .build();
        TestUtils.test(vhtConfig);

    }

    @Test(timeout = 180000)
    public void testBaggingWithThreads() throws Exception {
        TestParams baggingConfig = new TestParams.Builder()
                .setInputInstances(100_000)
                .setSamplingSize(10_000)
                .setInputDelayMs(0) // prevents saturating the system due to unbounded queues
                .setEvaluationInstances(90_000)
                .setClassifiedInstances(105_000)
                .setClassificationsCorrect(55f)
                .setKappaStat(0f)
                .setKappaTempStat(0f)
                .setCLIStringTemplate(TestParams.Templates.PREQEVAL_BAGGING_RANDOMTREE + " -t 2")
                .setPrePollWait(10)
                .setResultFilePollTimeout(30)
                .setTaskClassName(LocalThreadsDoTask.class.getName())
                .build();
        TestUtils.test(baggingConfig);

    }


}
