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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javacliparser.ClassOption;
import com.yahoo.labs.samoa.tasks.Task;
import com.yahoo.labs.samoa.topology.impl.SamzaComponentFactory;
import com.yahoo.labs.samoa.topology.impl.SamzaEngine;
import com.yahoo.labs.samoa.topology.impl.SamzaTopology;
import com.yahoo.labs.samoa.utils.SystemsUtils;

/**
 * Main class to run the task on Samza
 * 
 * @author Anh Thu Vu
 */
public class SamzaDoTask {

	private static final Logger logger = LoggerFactory.getLogger(SamzaDoTask.class);
	
	private static final String LOCAL_MODE = "local";
	private static final String YARN_MODE = "yarn";
	
	// FLAGS
	private static final String YARN_CONF_FLAG = "--yarn_home";
	private static final String MODE_FLAG = "--mode";
	private static final String ZK_FLAG = "--zookeeper";
	private static final String KAFKA_FLAG = "--kafka";
	private static final String KAFKA_REPLICATION_FLAG = "--kafka_replication_factor";
	private static final String CHECKPOINT_FREQ_FLAG = "--checkpoint_frequency";
	private static final String JAR_PACKAGE_FLAG = "--jar_package";
	private static final String SAMOA_HDFS_DIR_FLAG = "--samoa_hdfs_dir";
	private static final String AM_MEMORY_FLAG = "--yarn_am_mem";
	private static final String CONTAINER_MEMORY_FLAG = "--yarn_container_mem";
	private static final String PI_PER_CONTAINER_FLAG = "--pi_per_container";
	
	private static final String KRYO_REGISTER_FLAG = "--kryo_register";
	
	// config values
	private static int kafkaReplicationFactor = 1;
	private static int checkpointFrequency = 60000;
	private static String kafka = "localhost:9092";
	private static String zookeeper = "localhost:2181";
	private static boolean isLocal = true;
	private static String yarnConfHome = null;
	private static String samoaHDFSDir = null;
	private static String jarPackagePath = null;
	private static int amMem = 1024;
	private static int containerMem = 1024;
	private static int piPerContainer = 2;
	private static String kryoRegisterFile = null;
	
	/*
	 * 1. Read arguments
	 * 2. Construct topology/task
	 * 3. Upload the JAR to HDFS if we are running on YARN
	 * 4. Submit topology to SamzaEngine
	 */
	public static void main(String[] args) {
		// Read arguments
		List<String> tmpArgs = new ArrayList<String>(Arrays.asList(args));
		parseArguments(tmpArgs);
		
		args = tmpArgs.toArray(new String[0]);
		
		// Init Task
		StringBuilder cliString = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            cliString.append(" ").append(args[i]);
        }
        logger.debug("Command line string = {}", cliString.toString());
        System.out.println("Command line string = " + cliString.toString());
        
		Task task = null;
        try {
            task = (Task) ClassOption.cliStringToObject(cliString.toString(), Task.class, null);
            logger.info("Sucessfully instantiating {}", task.getClass().getCanonicalName());
        } catch (Exception e) {
            logger.error("Fail to initialize the task", e);
            System.out.println("Fail to initialize the task" + e);
            return;
        }
		task.setFactory(new SamzaComponentFactory());
		task.init();
		
		// Upload JAR file to HDFS
		String hdfsPath = null;
		if (!isLocal) {
			Path path = FileSystems.getDefault().getPath(jarPackagePath);
			hdfsPath = uploadJarToHDFS(path.toFile());
			if(hdfsPath == null) {
				System.out.println("Fail uploading JAR file \""+path.toAbsolutePath().toString()+"\" to HDFS.");
				return;
			}
		}
		
		// Set parameters
		SamzaEngine.getEngine()
		.setLocalMode(isLocal)
		.setZooKeeper(zookeeper)
		.setKafka(kafka)
		.setYarnPackage(hdfsPath)
		.setKafkaReplicationFactor(kafkaReplicationFactor)
		.setConfigHome(yarnConfHome)
		.setAMMemory(amMem)
		.setContainerMemory(containerMem)
		.setPiPerContainerRatio(piPerContainer)
		.setKryoRegisterFile(kryoRegisterFile)
		.setCheckpointFrequency(checkpointFrequency);
		
		// Submit topology
		SamzaEngine.submitTopology((SamzaTopology)task.getTopology());
		
	}
	
	private static boolean isLocalMode(String mode) {
		return mode.equals(LOCAL_MODE);
	}
	
	private static void parseArguments(List<String> args) {
		for (int i=args.size()-1; i>=0; i--) {
			String arg = args.get(i).trim();
			String[] splitted = arg.split("=",2);
			
			if (splitted.length >= 2) {
				// YARN config folder which contains conf/core-site.xml,
				// conf/hdfs-site.xml, conf/yarn-site.xml
				if (splitted[0].equals(YARN_CONF_FLAG)) {
					yarnConfHome = splitted[1];
					args.remove(i);
				}
				// host:port for zookeeper cluster
				else if (splitted[0].equals(ZK_FLAG)) {
					zookeeper = splitted[1];
					args.remove(i);
				}
				// host:port,... for kafka broker(s)
				else if (splitted[0].equals(KAFKA_FLAG)) {
					kafka = splitted[1];
					args.remove(i);
				}
				// whether we are running Samza in Local mode or YARN mode 
				else if (splitted[0].equals(MODE_FLAG)) {
					isLocal = isLocalMode(splitted[1]);
					args.remove(i);
				}
				// memory requirement for YARN application master
				else if (splitted[0].equals(AM_MEMORY_FLAG)) {
					amMem = Integer.parseInt(splitted[1]);
					args.remove(i);
				}
				// memory requirement for YARN worker container
				else if (splitted[0].equals(CONTAINER_MEMORY_FLAG)) {
					containerMem = Integer.parseInt(splitted[1]);
					args.remove(i);
				}
				// the path to JAR archive that we need to upload to HDFS
				else if (splitted[0].equals(JAR_PACKAGE_FLAG)) {
					jarPackagePath = splitted[1];
					args.remove(i);
				}
				// the HDFS dir for SAMOA files
				else if (splitted[0].equals(SAMOA_HDFS_DIR_FLAG)) {
					samoaHDFSDir = splitted[1];
					if (samoaHDFSDir.length() < 1) samoaHDFSDir = null;
					args.remove(i);
				}
				// number of max PI instances per container
				// this will be used to compute the number of containers 
				// AM will request for the job
				else if (splitted[0].equals(PI_PER_CONTAINER_FLAG)) {
					piPerContainer = Integer.parseInt(splitted[1]);
					args.remove(i);
				}
				// kafka streams replication factor
				else if (splitted[0].equals(KAFKA_REPLICATION_FLAG)) {
					kafkaReplicationFactor = Integer.parseInt(splitted[1]);
					args.remove(i);
				}
				// checkpoint frequency in ms
				else if (splitted[0].equals(CHECKPOINT_FREQ_FLAG)) {
					checkpointFrequency = Integer.parseInt(splitted[1]);
					args.remove(i);
				}
				// the file contains registration information for Kryo serializer
				else if (splitted[0].equals(KRYO_REGISTER_FLAG)) {
					kryoRegisterFile = splitted[1];
					args.remove(i);
				}
			}
		}
	}
	
	private static String uploadJarToHDFS(File file) {
		SystemsUtils.setHadoopConfigHome(yarnConfHome);
		SystemsUtils.setSAMOADir(samoaHDFSDir);
		return SystemsUtils.copyToHDFS(file, file.getName());
	}
}
