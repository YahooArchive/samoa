package com.yahoo.labs.samoa;

public class TestParams {

    /**
     *  templates that take the following parameters:
     *  <ul>
     *      <li>the output file location as an argument (-d),
     *      <li>the maximum number of instances for testing/training (-i)
     *      <li>the sampling size (-f)
     *      <li>the delay in ms between input instances (-w) , default is zero
     *  </ul>
     *  as well as the maximum number of instances for testing/training (-i) and the sampling size (-f)
     */
    public static class Templates {

        public final static String PREQEVAL_VHT_RANDOMTREE = "PrequentialEvaluation -d %s -i %d -f %d -w %d "
                + "-l (com.yahoo.labs.samoa.learners.classifiers.trees.VerticalHoeffdingTree -p 4) " +
                "-s (com.yahoo.labs.samoa.moa.streams.generators.RandomTreeGenerator -c 2 -o 10 -u 10)";

        public final static String PREQEVAL_NAIVEBAYES_HYPERPLANE = "PrequentialEvaluation -d %s -i %d -f %d -w %d "
                + "-l (classifiers.SingleClassifier -l com.yahoo.labs.samoa.learners.classifiers.NaiveBayes) " +
                "-s (com.yahoo.labs.samoa.moa.streams.generators.HyperplaneGenerator -c 2)";

        // setting the number of nominal attributes to zero significantly reduces the processing time,
        // so that it's acceptable in a test case
        public final static String PREQEVAL_BAGGING_RANDOMTREE = "PrequentialEvaluation -d %s -i %d -f %d -w %d "
                + "-l (com.yahoo.labs.samoa.learners.classifiers.ensemble.Bagging) " +
                "-s (com.yahoo.labs.samoa.moa.streams.generators.RandomTreeGenerator -c 2 -o 0 -u 10)";

    }


    public static final String EVALUATION_INSTANCES = "evaluation instances";
    public static final String CLASSIFIED_INSTANCES = "classified instances";
    public static final String CLASSIFICATIONS_CORRECT = "classifications correct (percent)";
    public static final String KAPPA_STAT = "Kappa Statistic (percent)";
    public static final String KAPPA_TEMP_STAT = "Kappa Temporal Statistic (percent)";


    private long inputInstances;
    private long samplingSize;
    private long evaluationInstances;
    private long classifiedInstances;
    private float classificationsCorrect;
    private float kappaStat;
    private float kappaTempStat;
    private String cliStringTemplate;
    private int pollTimeoutSeconds;
    private final int prePollWait;
    private int inputDelayMicroSec;
    private String taskClassName;

    private TestParams(String taskClassName,
                       long inputInstances,
                       long samplingSize,
                       long evaluationInstances,
                       long classifiedInstances,
                       float classificationsCorrect,
                       float kappaStat,
                       float kappaTempStat,
                       String cliStringTemplate,
                       int pollTimeoutSeconds,
                       int prePollWait,
                       int inputDelayMicroSec) {
        this.taskClassName = taskClassName;
        this.inputInstances = inputInstances;
        this.samplingSize = samplingSize;
        this.evaluationInstances = evaluationInstances;
        this.classifiedInstances = classifiedInstances;
        this.classificationsCorrect = classificationsCorrect;
        this.kappaStat = kappaStat;
        this.kappaTempStat = kappaTempStat;
        this.cliStringTemplate = cliStringTemplate;
        this.pollTimeoutSeconds = pollTimeoutSeconds;
        this.prePollWait = prePollWait;
        this.inputDelayMicroSec = inputDelayMicroSec;
    }

    public String getTaskClassName() {
        return taskClassName;
    }

    public long getInputInstances() {
        return inputInstances;
    }

    public long getSamplingSize() {
        return samplingSize;
    }

    public int getPollTimeoutSeconds() {
        return pollTimeoutSeconds;
    }

    public int getPrePollWaitSeconds() {
        return prePollWait;
    }

    public String getCliStringTemplate() {
        return cliStringTemplate;
    }

    public long getEvaluationInstances() {
        return evaluationInstances;
    }

    public long getClassifiedInstances() {
        return classifiedInstances;
    }

    public float getClassificationsCorrect() {
        return classificationsCorrect;
    }

    public float getKappaStat() {
        return kappaStat;
    }

    public float getKappaTempStat() {
        return kappaTempStat;
    }

    public int getInputDelayMicroSec() {
        return inputDelayMicroSec;
    }

    @Override
    public String toString() {
        return "TestParams{\n" +
                "inputInstances=" + inputInstances + "\n" +
                "samplingSize=" + samplingSize + "\n" +
                "evaluationInstances=" + evaluationInstances + "\n" +
                "classifiedInstances=" + classifiedInstances + "\n" +
                "classificationsCorrect=" + classificationsCorrect + "\n" +
                "kappaStat=" + kappaStat + "\n" +
                "kappaTempStat=" + kappaTempStat + "\n" +
                "cliStringTemplate='" + cliStringTemplate + '\'' + "\n" +
                "pollTimeoutSeconds=" + pollTimeoutSeconds + "\n" +
                "prePollWait=" + prePollWait + "\n" +
                "taskClassName='" + taskClassName + '\'' + "\n" +
                "inputDelayMicroSec=" + inputDelayMicroSec + "\n" +
                '}';
    }

    public static class Builder {
        private long inputInstances;
        private long samplingSize;
        private long evaluationInstances;
        private long classifiedInstances;
        private float classificationsCorrect;
        private float kappaStat =0f;
        private float kappaTempStat =0f;
        private String cliStringTemplate;
        private int pollTimeoutSeconds = 10;
        private int prePollWaitSeconds = 10;
        private String taskClassName;
        private int inputDelayMicroSec = 0;

        public Builder taskClassName(String taskClassName) {
            this.taskClassName = taskClassName;
            return this;
        }

        public Builder inputInstances(long inputInstances) {
            this.inputInstances = inputInstances;
            return this;
        }

        public Builder samplingSize(long samplingSize) {
            this.samplingSize = samplingSize;
            return this;
        }

        public Builder evaluationInstances(long evaluationInstances) {
            this.evaluationInstances = evaluationInstances;
            return this;
        }

        public Builder classifiedInstances(long classifiedInstances) {
            this.classifiedInstances = classifiedInstances;
            return this;
        }

        public Builder classificationsCorrect(float classificationsCorrect) {
            this.classificationsCorrect = classificationsCorrect;
            return this;
        }

        public Builder kappaStat(float kappaStat) {
            this.kappaStat = kappaStat;
            return this;
        }

        public Builder kappaTempStat(float kappaTempStat) {
            this.kappaTempStat = kappaTempStat;
            return this;
        }

        public Builder cliStringTemplate(String cliStringTemplate) {
            this.cliStringTemplate = cliStringTemplate;
            return this;
        }

        public Builder resultFilePollTimeout(int pollTimeoutSeconds) {
            this.pollTimeoutSeconds = pollTimeoutSeconds;
            return this;
        }

        public Builder inputDelayMicroSec(int inputDelayMicroSec) {
            this.inputDelayMicroSec = inputDelayMicroSec;
            return this;
        }

        public Builder prePollWait(int prePollWaitSeconds) {
            this.prePollWaitSeconds = prePollWaitSeconds;
            return this;
        }

        public TestParams build() {
            return new TestParams(taskClassName,
                    inputInstances,
                    samplingSize,
                    evaluationInstances,
                    classifiedInstances,
                    classificationsCorrect,
                    kappaStat,
                    kappaTempStat,
                    cliStringTemplate,
                    pollTimeoutSeconds,
                    prePollWaitSeconds,
                    inputDelayMicroSec);
        }
    }
}
