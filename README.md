<!--
  Copyright (c) 2013 Yahoo! Inc. All Rights Reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
SAMOA: Scalable Advanced Massive Online Analysis.
=================
SAMOA is a platform for mining on big data streams.
It is a distributed streaming machine learning (ML) framework that contains a 
programing abstraction for distributed streaming ML algorithms.

SAMOA enables development of new ML algorithms without dealing with 
the complexity of underlying streaming processing engines (SPE, such 
as Apache Storm and Apache S4). SAMOA also provides extensibility in integrating
new SPEs into the framework. These features allow SAMOA users to develop 
distributed streaming ML algorithms once and to execute the algorithms 
in multiple SPEs, i.e., code the algorithms once and execute them in multiple SPEs.

## Build

If you want to compile SAMOA for S4, you will need to install the S4 dependencies
manually as explained in [Executing SAMOA with Apache S4](../../wiki/1.2-Executing-SAMOA-with-Apache-S4).

Once the dependencies if needed are installed, you can simply clone the repository and install SAMOA.

```bash
git clone git@github.com:yahoo/samoa.git
cd samoa
mvn -P<variant> package # where variant is "storm" or "s4"

mvn -Pstorm package # e.g., to get the Storm version
```

The deployable jars for SAMOA will be in `target/SAMOA-<variant>-<version>.jar`.
For example, for Storm `target/SAMOA-Storm-0.0.1.jar`.

## Documentation

The documentation is intended to give an introduction on how to use SAMOA in the various different ways possible. 
As a user you can use it to develop new algorithms and test different Stream Processing Engines.

* [1 Scalable Advanced Massive Online Analysis](../../wiki/1 Scalable Advanced Massive Online Analysis)
    * [1.0 Building SAMOA](../../wiki/1.0 Building SAMOA)
    * [1.1 Executing SAMOA with Twitter Storm](../../wiki/1.1 Executing SAMOA with Twitter Storm)
    * [1.2 Executing SAMOA with Apache S4](../../wiki/1.2-Executing-SAMOA-with-Apache-S4)
* [2 SAMOA and Machine Learning](../../wiki/2 SAMOA and Machine Learning)
    * [2.1 Prequential Evaluation Task](../../wiki/2.1 Prequential Evaluation Task)
    * [2.2 Vertical Hoeffding Tree Classifier](../../wiki/2.2 Vertical Hoeffding Tree Classifier)
    * [2.3 Distributed Stream Clustering](../../wiki/2.3 Distributed Stream Clustering)
* [3 SAMOA Topology](../../wiki/3 SAMOA Topology)
    * [3.1 Processor](../../wiki/3.1 Processor)
    * [3.2 Processing Item](../../wiki/3.1 Processor)
    * [3.3 Content Event](../../wiki/3.3 Content Event)
    * [3.4 Stream](../../wiki/3.4 Stream)
    * [3.5 Task](../../wiki/3.5 Task)
    * [3.6 Topology Builder](../../wiki/3.6 Topology Builder)
    * [3.6 Topology Starter](../../wiki/3.6 Topology Starter)
* [4 Developing New Tasks in SAMOA](../../wiki/4 Developing New Tasks in SAMOA)


## License

The use and distribution terms for this software are covered by the
Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html).
