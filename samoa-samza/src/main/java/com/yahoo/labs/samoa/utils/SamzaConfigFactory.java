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
import com.yahoo.labs.samoa.topology.Stream;
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
	public static final String ZOOKEEPER_URI_KEY = "consumer.zookeeper.connect";
	public static final String BROKER_URI_KEY = "producer.metadata.broker.list";
	public static final String KAFKA_BATCHSIZE_KEY = "producer.batch.num.messages";
	public static final String KAFKA_PRODUCER_TYPE_KEY = "producer.producer.type";
	// SERDE
	public static final String SERDE_REGISTRATION_KEY = "kryo.register";

	// Instance variables
	private boolean isLocalMode;
	private String zookeeper;
	private String kafkaBrokerList;
	private int replicationFactor;
	private int amMemory;
	private int containerMemory;
	private int piPerContainerRatio;
	private int checkpointFrequency; // in ms

	private String jarPath;
	private String kryoRegisterFile = null;

	public SamzaConfigFactory() {
		this.isLocalMode = false;
		this.zookeeper = DEFAULT_ZOOKEEPER;
		this.kafkaBrokerList = DEFAULT_BROKER_LIST;
		this.checkpointFrequency = 60000; // default: 1 minute
		this.replicationFactor = 1;
	}

	/*
	 * Setter methods
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

	public SamzaConfigFactory setKafka(String brokerList) {
		this.kafkaBrokerList = brokerList;
		return this;
	}
	
	public SamzaConfigFactory setCheckpointFrequency(int freq) {
		this.checkpointFrequency = freq;
		return this;
	}
	
	public SamzaConfigFactory setReplicationFactor(int replicationFactor) {
		this.replicationFactor = replicationFactor;
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
		
		List<String> nameList = new ArrayList<String>();
		// Default kafka system: kafka0: sync producer
		// This system is always required: it is used for checkpointing
		nameList.add("kafka0");
		setKafkaSystem(map, "kafka0", this.zookeeper, this.kafkaBrokerList, 1);
		// Output streams: set kafka systems
		for (SamzaStream stream:pi.getOutputStreams()) {
			boolean found = false;
			for (String name:nameList) {
				if (stream.getSystemName().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				nameList.add(stream.getSystemName());
				setKafkaSystem(map, stream.getSystemName(), this.zookeeper, this.kafkaBrokerList, stream.getBatchSize());
			}
		}
		// Input streams: set kafka systems
		for (SamzaSystemStream stream:pi.getInputStreams()) {
			boolean found = false;
			for (String name:nameList) {
				if (stream.getSystem().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				nameList.add(stream.getSystem());
				setKafkaSystem(map, stream.getSystem(), this.zookeeper, this.kafkaBrokerList, 1);
			}
		}
		
		// Checkpointing
		setValue(map,"task.checkpoint.factory","org.apache.samza.checkpoint.kafka.KafkaCheckpointManagerFactory");
		setValue(map,"task.checkpoint.system","kafka0");
		setValue(map,"task.commit.ms","1000");
		setValue(map,"task.checkpoint.replication.factor",Integer.toString(this.replicationFactor));
		
		// Number of containers
		setNumberOfContainers(map, pi.getParallelism(), this.piPerContainerRatio);

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
		// Since entrancePI should have only 1 output stream
		// there is no need for checking the batch size, setting different system names
		// The custom consumer (samoa system) does not suuport reading from a specific index
		// => no need for checkpointing
		SamzaStream outputStream = (SamzaStream)epi.getOutputStream();
		// Set samoa system factory
		setValue(map, "systems."+SYSTEM_NAME+".samza.factory", SamoaSystemFactory.class.getName());
		// Set Kafka system (only if there is an output stream)
		if (outputStream != null)
			setKafkaSystem(map, outputStream.getSystemName(), this.zookeeper, this.kafkaBrokerList, outputStream.getBatchSize());

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
		String dstPath = filePath.toString();
		String resPath;
		String filesystem;
		if (this.isLocalMode) {
			filesystem = SystemsUtils.LOCAL_FS;
			File dir = dirPath.toFile();
			if (!dir.exists()) 
				FileUtils.forceMkdir(dir);
		}
		else {
			filesystem = SystemsUtils.HDFS;
		}

		// Correct system name for streams
		this.setSystemNameForStreams(topology.getStreams());
		
		// Add all PIs to a collection (map)
		Map<String,Object> piMap = new HashMap<String,Object>();
		Set<EntranceProcessingItem> entranceProcessingItems = topology.getEntranceProcessingItems();
		Set<IProcessingItem> processingItems = topology.getNonEntranceProcessingItems();
		for(EntranceProcessingItem epi:entranceProcessingItems) {
			SamzaEntranceProcessingItem sepi = (SamzaEntranceProcessingItem) epi;
			piMap.put(sepi.getName(), sepi);
		}
		for(IProcessingItem pi:processingItems) {
			SamzaProcessingItem spi = (SamzaProcessingItem) pi;
			piMap.put(spi.getName(), spi);
		}

		// Serialize all PIs
		boolean serialized = false;
		if (this.isLocalMode) {
			serialized = SystemsUtils.serializeObjectToLocalFileSystem(piMap, dstPath);
			resPath = dstPath;
		}
		else {
			resPath = SystemsUtils.serializeObjectToHDFS(piMap, dstPath);
			serialized = resPath != null;
		}

		if (!serialized) {
			throw new Exception("Fail serialize map of PIs to file");
		}

		// MapConfig for all PIs
		for(EntranceProcessingItem epi:entranceProcessingItems) {
			SamzaEntranceProcessingItem sepi = (SamzaEntranceProcessingItem) epi;
			maps.add(this.getMapForEntrancePI(sepi, resPath, filesystem));
		}
		for(IProcessingItem pi:processingItems) {
			SamzaProcessingItem spi = (SamzaProcessingItem) pi;
			maps.add(this.getMapForPI(spi, resPath, filesystem));
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
	 *
	 */
	public void setSystemNameForStreams(Set<Stream> streams) {
		Map<Integer, String> batchSizeMap = new HashMap<Integer, String>();
		batchSizeMap.put(1, "kafka0"); // default system with sync producer
		int counter = 0;
		for (Stream stream:streams) {
			SamzaStream samzaStream = (SamzaStream) stream;
			String systemName = batchSizeMap.get(samzaStream.getBatchSize());
			if (systemName == null) {
				counter++;
				// Add new system
				systemName = "kafka"+Integer.toString(counter);
				batchSizeMap.put(samzaStream.getBatchSize(), systemName);
			}
			samzaStream.setSystemName(systemName);
		}

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
			map.put(YARN_PACKAGE_KEY,jarPath);
			map.put(CONTAINER_MEMORY_KEY, Integer.toString(this.containerMemory));
			map.put(AM_MEMORY_KEY, Integer.toString(this.amMemory));
			map.put(CONTAINER_COUNT_KEY, "1"); 
			map.put(YARN_CONF_HOME_KEY, SystemsUtils.getHadoopConfigHome());
			
			// Task opts (Heap size = 0.75 container memory) 
			int heapSize = (int)(0.75*this.containerMemory);
			map.put("task.opts", "-Xmx"+Integer.toString(heapSize)+"M -XX:+PrintGCDateStamps");
		}


		map.put(JOB_NAME_KEY, "");
		map.put(TASK_CLASS_KEY, "");
		map.put(TASK_INPUTS_KEY, "");

		// register serializer
		map.put("serializers.registry.kryo.class",SamzaKryoSerdeFactory.class.getName());

		// Serde registration
		setKryoRegistration(map, this.kryoRegisterFile);

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
	
	private static void setKafkaSystem(Map<String,String> map, String systemName, String zk, String brokers, int batchSize) {
		map.put("systems."+systemName+".samza.factory",KafkaSystemFactory.class.getName());
		map.put("systems."+systemName+".samza.msg.serde","kryo");

		map.put("systems."+systemName+"."+ZOOKEEPER_URI_KEY,zk);
		map.put("systems."+systemName+"."+BROKER_URI_KEY,brokers);
		map.put("systems."+systemName+"."+KAFKA_BATCHSIZE_KEY,Integer.toString(batchSize));

		map.put("systems."+systemName+".samza.offset.default","oldest");

		if (batchSize > 1) {
			map.put("systems."+systemName+"."+KAFKA_PRODUCER_TYPE_KEY,"async");
		}
		else {
			map.put("systems."+systemName+"."+KAFKA_PRODUCER_TYPE_KEY,"sync");
		}
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
