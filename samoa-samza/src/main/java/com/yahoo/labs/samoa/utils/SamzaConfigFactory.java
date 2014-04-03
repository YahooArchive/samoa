package com.yahoo.labs.samoa.utils;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.samza.config.MapConfig;
import org.apache.samza.job.local.LocalJobFactory;
import org.apache.samza.job.yarn.YarnJobFactory;
import org.apache.samza.system.kafka.KafkaSystemFactory;

import com.yahoo.labs.samoa.topology.EntranceProcessingItem;
import com.yahoo.labs.samoa.topology.IProcessingItem;
import com.yahoo.labs.samoa.topology.ProcessingItem;
import com.yahoo.labs.samoa.topology.impl.SamoaSystemFactory;
import com.yahoo.labs.samoa.topology.impl.SamzaEntranceProcessingItem;
import com.yahoo.labs.samoa.topology.impl.SamzaProcessingItem;
import com.yahoo.labs.samoa.topology.impl.SamzaStream;
import com.yahoo.labs.samoa.topology.impl.SamzaTopology;
import com.yahoo.labs.samoa.topology.impl.SamzaStream.SamzaSystemStream;

/**
 * Generate Configs that will be used to submit Samza jobs
 * from the input topology (one config per PI/EntrancePI in 
 * the topology)
 * 
 * @author Anh Thu Vu
 *
 */
public class SamzaConfigFactory {
	public static final String SYSTEM_NAME = "samoa";
	
	// DEFAULT VALUES
	private static final String DEFAULT_ZOOKEEPER = "localhost:2181";
	private static final String DEFAULT_BROKER_LIST = "localhost:9092";

	// DELIMINATORS
	public static final String COMMA = ",";
	public static final String COLON = ":";
	public static final String DOT = ".";
	public static final char DOLLAR_SIGN = '$';
	public static final char QUESTION_MARK = '?';
	
	// PARTITIONING SCHEMES
	public static final String SHUFFLE = "shuffle";
	public static final String KEY = "key";
	public static final String BROADCAST = "broadcast";

	// PROPERTY KEYS
	// JOB
	public static final String JOB_FACTORY_CLASS_KEY = "job.factory.class";
	public static final String JOB_NAME_KEY = "job.name";
	// YARN 
	public static final String YARN_PACKAGE_KEY = "yarn.package.path";
	public static final String CONTAINER_MEMORY_KEY = "yarn.container.memory.mb";
	public static final String AM_MEMORY_KEY = "yarn.am.container.memory.mb";
	public static final String CONTAINER_COUNT_KEY = "yarn.container.count";
	// TASK (SAMZA original)
	public static final String TASK_CLASS_KEY = "task.class";
	public static final String TASK_INPUTS_KEY = "task.inputs";
	// TASK (extra)
	public static final String FILE_KEY = "task.processor.file";
	public static final String FILESYSTEM_KEY = "task.processor.filesystem";
	public static final String ENTRANCE_INPUT_KEY = "task.entrance.input";
	public static final String ENTRANCE_OUTPUT_KEY = "task.entrance.outputs";
	public static final String YARN_CONF_HOME_KEY = "yarn.config.home";
	// KAFKA
	public static final String ZOOKEEPER_URI_KEY = "systems.kafka.consumer.zookeeper.connect";
	public static final String BROKER_URI_KEY = "systems.kafka.producer.metadata.broker.list";
	public static final String KAFKA_BATCHSIZE_KEY = "systems.kafka.producer.batch.num.messages";
	// SERDE
	public static final String SERDE_REGISTRATION_KEY = "kryo.register";

	// Instance variables
	private boolean isLocalMode;
	private String zookeeper;
	private String kafkaBrokerList;
	private int kafkaBatchSize;
	private String kafkaProducerType;
	private int amMemory;
	private int containerMemory;
	private int piPerContainerRatio;

	private String jarPath;
	private String kryoRegisterFile = null;

	public SamzaConfigFactory() {
		this.isLocalMode = false;
		this.zookeeper = DEFAULT_ZOOKEEPER;
		this.kafkaBrokerList = DEFAULT_BROKER_LIST;
		this.kafkaBatchSize = 1;
		this.kafkaProducerType = "sync";
	}

	/*
	 * Builder methods
	 */
	public SamzaConfigFactory setYarnPackage(String packagePath) {
		this.jarPath = packagePath;
		return this;
	}

	public SamzaConfigFactory setLocalMode(boolean isLocal) {
		this.isLocalMode = isLocal;
		return this;
	}

	public SamzaConfigFactory setZookeeper(String zk) {
		this.zookeeper = zk;
		return this;
	}

	public SamzaConfigFactory setKafka(String brokerList, String prodType, int batchSize) {
		this.kafkaBrokerList = brokerList;
		this.kafkaBatchSize = batchSize;
		this.kafkaProducerType = prodType;
		return this;
	}

	public SamzaConfigFactory setAMMemory(int mem) {
		this.amMemory = mem;
		return this;
	}

	public SamzaConfigFactory setContainerMemory(int mem) {
		this.containerMemory = mem;
		return this;
	}
	
	public SamzaConfigFactory setPiPerContainerRatio(int piPerContainer) {
		this.piPerContainerRatio = piPerContainer;
		return this;
	}

	public SamzaConfigFactory setKryoRegisterFile(String kryoRegister) {
		this.kryoRegisterFile = kryoRegister;
		return this;
	}

	/*
	 * Generate a map of all config properties for the input SamzaProcessingItem
	 */
	private Map<String,String> getMapForPI(SamzaProcessingItem pi, String filename, String filesystem) throws Exception {
		Map<String,String> map = getBasicSystemConfig();

		// Set job name, task class, task inputs (from SamzaProcessingItem)
		setJobName(map, pi.getName());
		setTaskClass(map, SamzaProcessingItem.class.getName());

		StringBuilder streamNames = new StringBuilder();
		boolean first = true;
		for(SamzaSystemStream stream:pi.getInputStreams()) {
			if (!first) streamNames.append(COMMA);
			streamNames.append(stream.getSystem()+DOT+stream.getStream());
			if (first) first = false;
		}
		setTaskInputs(map, streamNames.toString());

		// Processor file
		setFileName(map, filename);
		setFileSystem(map, filesystem);
		
		// Number of containers
		setNumberOfContainers(map, pi.getParalellism(), this.piPerContainerRatio);

		return map;
	}

	/*
	 * Generate a map of all config properties for the input SamzaProcessingItem
	 */
	public Map<String,String> getMapForEntrancePI(SamzaEntranceProcessingItem epi, String filename, String filesystem) {
		Map<String,String> map = getBasicSystemConfig();

		// Set job name, task class (from SamzaEntranceProcessingItem)
		setJobName(map, epi.getName());
		setTaskClass(map, SamzaEntranceProcessingItem.class.getName());

		// Input for the entrance task (from our custom consumer)
		setTaskInputs(map, SYSTEM_NAME+"."+epi.getName());

		// Output from entrance task
		SamzaStream outputStream = epi.getOutputStream();
		StringBuilder allStreams = new StringBuilder();
		boolean first = true;
		for (SamzaSystemStream stream:outputStream.getSystemStreams()) {
			if (!first) allStreams.append(COMMA);

			// Name (system.stream)
			allStreams.append(stream.getSystem());
			allStreams.append(COLON);
			allStreams.append(stream.getStream());
			allStreams.append(COLON);

			// Type
			switch(stream.getPartitioningScheme())  {
			case SHUFFLE:
				allStreams.append(SamzaConfigFactory.SHUFFLE);
				break;
			case GROUP_BY_KEY:
				allStreams.append(SamzaConfigFactory.KEY);
				break;
			case BROADCAST:
				allStreams.append(SamzaConfigFactory.BROADCAST);
				break;
			}
			allStreams.append(COLON);

			// Parallelism
			allStreams.append(stream.getParallelism());

		}
		setValue(map, ENTRANCE_OUTPUT_KEY, allStreams.toString());

		// Set samoa system factory
		setValue(map, "systems."+SYSTEM_NAME+".samza.factory", SamoaSystemFactory.class.getName());

		// Processor file
		setFileName(map, filename);
		setFileSystem(map, filesystem);
		
		// Number of containers
		setNumberOfContainers(map, 1, this.piPerContainerRatio);

		return map;
	}

	/*
	 * Generate a list of map (of config properties) for all PIs and EPI in 
	 * the input topology
	 */
	public List<Map<String,String>> getMapsForTopology(SamzaTopology topology) throws Exception {

		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();

		// File to write serialized objects
		String filename = topology.getTopologyName() + ".dat";
		Path dirPath = FileSystems.getDefault().getPath("dat");
		Path filePath= FileSystems.getDefault().getPath(dirPath.toString(), filename);
		String resPath;
		String filesystem;
		if (this.isLocalMode) {
			resPath = filePath.toString();
			filesystem = SystemsUtils.LOCAL_FS;
		}
		else {
			resPath = SystemsUtils.getHDFSNameNodeUri()+"/samoa/dat/"+filename;
			filesystem = SystemsUtils.HDFS;
		}

		File dir = dirPath.toFile();
		if (!dir.exists()) 
			FileUtils.forceMkdir(dir);

		Map<String,Object> piMap = new HashMap<String,Object>();
		Set<EntranceProcessingItem> entranceProcessingItems = topology.getEntranceProcessingItems();
		Set<IProcessingItem> processingItems = topology.getNonEntranceProcessingItems();
		for(EntranceProcessingItem epi:entranceProcessingItems) {
			SamzaEntranceProcessingItem sepi = (SamzaEntranceProcessingItem) epi;
			piMap.put(sepi.getName(), sepi);
			maps.add(this.getMapForEntrancePI(sepi, resPath, filesystem));
		}
		for(IProcessingItem pi:processingItems) {
			SamzaProcessingItem spi = (SamzaProcessingItem) pi;
			piMap.put(spi.getName(), spi);
			maps.add(this.getMapForPI(spi, resPath, filesystem));
		}

		// Serialize all PIs
		boolean serialized = false;
		if (this.isLocalMode) {
			serialized = SystemsUtils.serializeObjectToLocalFileSystem(piMap, resPath);
		}
		else {
			serialized = SystemsUtils.serializeObjectToHDFS(piMap, resPath);
		}

		if (!serialized) {
			throw new Exception("Fail serialize map of PIs to file");
		}
		return maps;
	}

	/**
	 * Construct a list of MapConfigs for a Topology
	 * @return the list of MapConfigs
	 * @throws Exception 
	 */
	public List<MapConfig> getMapConfigsForTopology(SamzaTopology topology) throws Exception {
		List<MapConfig> configs = new ArrayList<MapConfig>();
		List<Map<String,String>> maps = this.getMapsForTopology(topology);
		for(Map<String,String> map:maps) {
			configs.add(new MapConfig(map));
		}
		return configs;
	}

	/*
	 * Generate a map with common properties for PIs and EPI
	 */
	private Map<String,String> getBasicSystemConfig() {
		Map<String,String> map = new HashMap<String,String>();
		// Job & Task
		if (this.isLocalMode) 
			map.put(JOB_FACTORY_CLASS_KEY, LocalJobFactory.class.getName());
		else {
			map.put(JOB_FACTORY_CLASS_KEY, YarnJobFactory.class.getName());

			// yarn
			map.put(YARN_PACKAGE_KEY,SystemsUtils.getHDFSNameNodeUri()+jarPath);
			map.put(CONTAINER_MEMORY_KEY, Integer.toString(this.containerMemory));
			map.put(AM_MEMORY_KEY, Integer.toString(this.amMemory));
			map.put(CONTAINER_COUNT_KEY, "1"); // TODO: should it = parallelism?
			map.put(YARN_CONF_HOME_KEY, SystemsUtils.getHadoopConfigHome());
		}


		map.put(JOB_NAME_KEY, "");
		map.put(TASK_CLASS_KEY, "");
		map.put(TASK_INPUTS_KEY, "");

		// register serializer
		map.put("serializers.registry.kryo.class",SamzaKryoSerdeFactory.class.getName());

		// Kafka
		map.put("systems.kafka.samza.factory",KafkaSystemFactory.class.getName());
		map.put("systems.kafka.samza.msg.serde","kryo");

		setKryoRegistration(map, this.kryoRegisterFile);

		map.put(ZOOKEEPER_URI_KEY,this.zookeeper);
		map.put(BROKER_URI_KEY,this.kafkaBrokerList);

		map.put("systems.kafka.producer.producer.type",this.kafkaProducerType);
		map.put(KAFKA_BATCHSIZE_KEY,Integer.toString(this.kafkaBatchSize));

		map.put("systems.kafka.samza.offset.default","oldest");

		return map;
	}
	
	/*
	 * Helper methods to set different properties in the input map
	 */
	private static void setJobName(Map<String,String> map, String jobName) {
		map.put(JOB_NAME_KEY, jobName);
	}

	private static void setFileName(Map<String,String> map, String filename) {
		map.put(FILE_KEY, filename);
	}

	private static void setFileSystem(Map<String,String> map, String filesystem) {
		map.put(FILESYSTEM_KEY, filesystem);
	}

	private static void setTaskClass(Map<String,String> map, String taskClass) {
		map.put(TASK_CLASS_KEY, taskClass);
	}

	private static void setTaskInputs(Map<String,String> map, String inputs) {
		map.put(TASK_INPUTS_KEY, inputs);
	}

	private static void setKryoRegistration(Map<String, String> map, String kryoRegisterFile) {
		if (kryoRegisterFile != null) {
			String value = readKryoRegistration(kryoRegisterFile);
			map.put(SERDE_REGISTRATION_KEY,  value);
		}
	}
	
	private static void setNumberOfContainers(Map<String, String> map, int parallelism, int piPerContainer) {
		int res = parallelism / piPerContainer;
		if (parallelism % piPerContainer != 0) res++;
		map.put(CONTAINER_COUNT_KEY, Integer.toString(res));
	}
	
	// Set custom properties
	private static void setValue(Map<String,String> map, String key, String value) {
		map.put(key,value);
	}

	/*
	 * Helper method to parse Kryo registration file
	 */
	private static String readKryoRegistration(String filePath) {
		FileInputStream fis = null;
		Properties props = new Properties();
		StringBuilder result = new StringBuilder();
		try {
			fis = new FileInputStream(filePath);
			props.load(fis);

			boolean first = true;
			String value = null;
			for(String k:props.stringPropertyNames()) {
				if (!first)
					result.append(COMMA);
				else
					first = false;
				
				// Need to avoid the dollar sign as samza pass all the properties in
				// the config to containers via commandline parameters/enviroment variables
				// We might escape the dollar sign, but it's more complicated than
				// replacing it with something else
				result.append(k.trim().replace(DOLLAR_SIGN, QUESTION_MARK));
				value = props.getProperty(k);
				if (value != null && value.trim().length() > 0) {
					result.append(COLON);
					result.append(value.trim().replace(DOLLAR_SIGN, QUESTION_MARK));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return result.toString();
	}
}
