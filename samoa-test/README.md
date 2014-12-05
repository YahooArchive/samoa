This module contains a test framework for simplifying regression testing of Samoa algorithms on various platforms.

The test framework is generic and reusable for multiple platforms. The platform modules that make use of the test framework add a maven dependency to a test-jar artifact of the samoa-test module. This test-jar artifact includes the test framework classes and its dependencies. 

For defining tests, we reuse the code from the test framework but customize tests according to the platform capabilities.

For each algorithm to test, we must provide :

* the task class for the platform
* the algorithm (referring to the provided string templates in this module)
* the input parameters
* the expectations (thresholds or values)

See existing code in samo-local, samoa-threads and samoa-storm for some examples.
