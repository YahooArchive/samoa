package com.yahoo.labs.samoa;/*
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TestUtils.class.getName());


    public static void test(final TestParams testParams) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InterruptedException {

        final File tempFile = File.createTempFile("test", "test");

        LOG.info("Starting test, output file is {}, test config is \n{}", tempFile.getAbsolutePath(), testParams.toString());

        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    Class.forName(testParams.getTaskClassName())
                            .getMethod("main", String[].class)
                            .invoke(null, (Object) String.format(
                                    testParams.getCliStringTemplate(),
                                    tempFile.getAbsolutePath(),
                                    testParams.getInputInstances(),
                                    testParams.getSamplingSize(),
                                    testParams.getInputDelayMicroSec()
                                    ).split("[ ]"));
                } catch (Exception e) {
                    LOG.error("Cannot execute test {} {}", e.getMessage(), e.getCause().getMessage());
                }
                return null;
            }
        });

        Thread.sleep(TimeUnit.SECONDS.toMillis(testParams.getPrePollWaitSeconds()));

        CountDownLatch signalComplete = new CountDownLatch(1);

        final Tailer tailer = Tailer.create(tempFile, new TestResultsTailerAdapter(signalComplete), 1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tailer.run();
            }
        }).start();

        signalComplete.await();
        tailer.stop();

        assertResults(tempFile, testParams);
    }

    public static void assertResults(File outputFile, com.yahoo.labs.samoa.TestParams testParams) throws IOException {

        LOG.info("Checking results file " + outputFile.getAbsolutePath());
        // 1. parse result file with csv parser
        Reader in = new FileReader(outputFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withSkipHeaderRecord(false)
                .withIgnoreEmptyLines(true).withDelimiter(',').withCommentMarker('#').parse(in);
        CSVRecord last = null;
        Iterator<CSVRecord> iterator = records.iterator();
        CSVRecord header = iterator.next();
        Assert.assertEquals("Invalid number of columns", 5, header.size());

        Assert.assertEquals("Unexpected column", com.yahoo.labs.samoa.TestParams.EVALUATION_INSTANCES, header.get(0).trim());
        Assert.assertEquals("Unexpected column", com.yahoo.labs.samoa.TestParams.CLASSIFIED_INSTANCES, header.get(1).trim());
        Assert.assertEquals("Unexpected column", com.yahoo.labs.samoa.TestParams.CLASSIFICATIONS_CORRECT, header.get(2).trim());
        Assert.assertEquals("Unexpected column", com.yahoo.labs.samoa.TestParams.KAPPA_STAT, header.get(3).trim());
        Assert.assertEquals("Unexpected column", com.yahoo.labs.samoa.TestParams.KAPPA_TEMP_STAT, header.get(4).trim());

        // 2. check last line result
        while (iterator.hasNext()) {
            last = iterator.next();
        }

        assertTrue(String.format("Unmet threshold expected %d got %f",
                testParams.getEvaluationInstances(), Float.parseFloat(last.get(0))),
                testParams.getEvaluationInstances() <= Float.parseFloat(last.get(0)));
        assertTrue(String.format("Unmet threshold expected %d got %f", testParams.getClassifiedInstances(),
                Float.parseFloat(last.get(1))),
                testParams.getClassifiedInstances() <= Float.parseFloat(last.get(1)));
        assertTrue(String.format("Unmet threshold expected %f got %f",
                testParams.getClassificationsCorrect(), Float.parseFloat(last.get(2))),
                testParams.getClassificationsCorrect() <= Float.parseFloat(last.get(2)));
        assertTrue(String.format("Unmet threshold expected %f got %f",
                testParams.getKappaStat(), Float.parseFloat(last.get(3))),
                testParams.getKappaStat() <= Float.parseFloat(last.get(3)));
        assertTrue(String.format("Unmet threshold expected %f got %f",
                testParams.getKappaTempStat(), Float.parseFloat(last.get(4))),
                testParams.getKappaTempStat() <= Float.parseFloat(last.get(4)));

    }


    private static class TestResultsTailerAdapter extends TailerListenerAdapter {

        private final CountDownLatch signalComplete;

        public TestResultsTailerAdapter(CountDownLatch signalComplete) {
            this.signalComplete = signalComplete;
        }

        @Override
        public void handle(String line) {
            if ("# COMPLETED".equals(line.trim())) {
                signalComplete.countDown();
            }
        }
    }




}
