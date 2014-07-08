#!/bin/bash

###
# #%L
# SAMOA
# %%
# Copyright (C) 2013 - 2014 Yahoo! Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

if [ $# -lt 1 ];
then
  echo "USAGE: $0 classname [opts]"
  exit 1
fi

home_dir=`pwd`
base_dir=$(dirname $0)/..
cd $base_dir
base_dir=`pwd`
cd $home_dir

YARN_HOME="${YARN_HOME:-$HOME/.samza}"
#CLASSPATH=$base_dir:$YARN_HOME/conf
CLASSPATH=$base_dir/target/SAMOA-Samza-*.jar
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

if [ -z "$SAMZA_LOG_DIR" ]; then
  SAMZA_LOG_DIR="$base_dir"
fi

if [ -z "$SAMZA_CONTAINER_NAME" ]; then
  SAMZA_CONTAINER_NAME="undefined-samza-container-name"
fi

if [ -z "$JAVA_OPTS" ]; then
  JAVA_OPTS="-Xmx768M -XX:+PrintGCDateStamps"
fi

if [ -f $base_dir/lib/log4j.xml ]; then
  JAVA_OPTS="$JAVA_OPTS -Dlog4j.configuration=file:$base_dir/log4j.xml"
fi

JAVA_OPTS="$JAVA_OPTS -Xloggc:$SAMZA_LOG_DIR/gc.log -Dsamza.log.dir=$SAMZA_LOG_DIR -Dsamza.container.name=$SAMZA_CONTAINER_NAME"

echo $JAVA $JAVA_OPTS -cp $CLASSPATH $@
exec $JAVA $JAVA_OPTS -cp $CLASSPATH $@
