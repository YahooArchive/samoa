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

###Storm mode

Simply clone the repository and install SAMOA.
```bash
git clone git@github.com:yahoo/samoa.git
cd samoa
mvn -Pstorm package
```

The deployable jar for SAMOA will be in `target/SAMOA-Storm-0.0.1-SNAPSHOT.jar`.

###S4 mode

If you want to compile SAMOA for S4, you will need to install the S4 dependencies
manually as explained in [Executing SAMOA with Apache S4](../../wiki/Executing-SAMOA-with-Apache-S4).

Once the dependencies if needed are installed, you can simply clone the repository and install SAMOA.

```bash
git clone git@github.com:yahoo/samoa.git
cd samoa
mvn -Ps4 package
```

###Local mode

If you want to test SAMOA in a local environment, simply clone the repository and install SAMOA.

```bash
git clone git@github.com:yahoo/samoa.git
cd samoa
mvn package
```

The deployable jar for SAMOA will be in `target/SAMOA-Local-0.0.1-SNAPSHOT.jar`.

## Documentation

The documentation is intended to give an introduction on how to use SAMOA in the various different ways possible. 
As a user you can use it to develop new algorithms and test different Stream Processing Engines.

* [0 Getting Started in 5 minutes](../../wiki/Getting Started)

* [1 Scalable Advanced Massive Online Analysis](../../wiki/Scalable Advanced Massive Online Analysis)
    * [1.0 Building SAMOA](../../wiki/Building SAMOA)
    * [1.1 Executing SAMOA with Apache Storm](../../wiki/Executing SAMOA with Apache Storm)
    * [1.2 Executing SAMOA with Apache S4](../../wiki/Executing-SAMOA-with-Apache-S4)
* [2 SAMOA and Machine Learning](../../wiki/SAMOA and Machine Learning)
    * [2.1 Prequential Evaluation Task](../../wiki/Prequential Evaluation Task)
    * [2.2 Vertical Hoeffding Tree Classifier](../../wiki/Vertical Hoeffding Tree Classifier)
    * [2.3 Distributed Stream Clustering](../../wiki/Distributed Stream Clustering)
* [3 SAMOA Topology](../../wiki/SAMOA Topology)
    * [3.1 Processor](../../wiki/Processor)
    * [3.2 Content Event](../../wiki/Content Event)
    * [3.3 Stream](../../wiki/Stream)
    * [3.4 Task](../../wiki/Task)
    * [3.5 Topology Builder](../../wiki/Topology Builder)
    * [3.6 Topology Starter](../../wiki/Topology Starter)
    * [3.7 Learner](../../wiki/Learner)
    * [3.8 Processing Item](../../wiki/Processing Item)
* [4 Developing New Tasks in SAMOA](../../wiki/Developing New Tasks in SAMOA)

## Slides

[![SAMOA Slides](http://yahoo.github.io/samoa/samoa-slides.jpg)](https://speakerdeck.com/gdfm/samoa-a-platform-for-mining-big-data-streams)

G. De Francisci Morales [SAMOA: A Platform for Mining Big Data Streams](http://melmeric.files.wordpress.com/2013/04/samoa-a-platform-for-mining-big-data-streams.pdf)
Keynote Talk at [RAMSS ’13](http://www.ramss.ws/2013/program/): 2nd International Workshop on Real-Time Analysis and Mining of Social Streams WWW, Rio De Janeiro, 2013.

## SAMOA Developer's Guide

<p><a href="http://yahoo.github.io/samoa/SAMOA-Developers-Guide-0-0-1.pdf"><img style="max-width:95%;border:3px solid black;" src="http://yahoo.github.io/samoa/Manual.png" alt="SAMOA Developer's guide" height="250"> </a></p>

## Contributors
[List of contributors to the SAMOA project](http://yahoo.github.io/samoa/contributors.html)

## License

The use and distribution terms for this software are covered by the
Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html).

