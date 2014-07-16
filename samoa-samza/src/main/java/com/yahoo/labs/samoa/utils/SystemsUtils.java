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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities methods for:
 * - Kafka
 * - HDFS
 * - Handling files on local FS
 * 
 * @author Anh Thu Vu
 */
public class SystemsUtils {
	private static final Logger logger = LoggerFactory.getLogger(SystemsUtils.class);
	
	public static final String HDFS = "hdfs";
	public static final String LOCAL_FS = "local";
	
	private static final String TEMP_FILE = "samoaTemp";
	private static final String TEMP_FILE_SUFFIX = ".dat";
	
	/*
	 * Kafka
	 */
	private static class KafkaUtils {
		private static ZkClient zkClient;
		
		static void setZookeeper(String zk) {
			zkClient = new ZkClient(zk, 30000, 30000, new ZKStringSerializerWrapper());
		}

		/*
		 * Create Kafka topic/stream
		 */
		static void createKafkaTopic(String name, int partitions, int replicas) {
			AdminUtils.createTopic(zkClient, name, partitions, replicas, new Properties());
		}
		
		static class ZKStringSerializerWrapper implements ZkSerializer {
			@Override
			public Object deserialize(byte[] byteArray) throws ZkMarshallingError {
				return ZKStringSerializer.deserialize(byteArray);
			}

			@Override
			public byte[] serialize(Object obj) throws ZkMarshallingError {
				return ZKStringSerializer.serialize(obj);
			}
		}
	}
	
	/*
	 * HDFS
	 */
	private static class HDFSUtils {
		private static String coreConfPath;
		private static String hdfsConfPath;
		private static String configHomePath;
		private static String samoaDir = null;
		
		static void setHadoopConfigHome(String hadoopConfPath) {
			logger.info("Hadoop config home:{}",hadoopConfPath);
			configHomePath = hadoopConfPath;
			java.nio.file.Path coreSitePath = FileSystems.getDefault().getPath(hadoopConfPath, "core-site.xml");
			java.nio.file.Path hdfsSitePath = FileSystems.getDefault().getPath(hadoopConfPath, "hdfs-site.xml");
			coreConfPath = coreSitePath.toAbsolutePath().toString();
			hdfsConfPath = hdfsSitePath.toAbsolutePath().toString();
		}
		
		static String getNameNodeUri() {
			Configuration config = new Configuration();
			config.addResource(new Path(coreConfPath));
			config.addResource(new Path(hdfsConfPath));
			
			return config.get("fs.defaultFS");
		}
		
		static String getHadoopConfigHome() {
			return configHomePath;
		}
		
		static void setSAMOADir(String dir) {
			if (dir != null)
				samoaDir = getNameNodeUri()+dir;
			else 
				samoaDir = null;
		}
		
		static String getDefaultSAMOADir() throws IOException {
			Configuration config = new Configuration();
			config.addResource(new Path(coreConfPath));
			config.addResource(new Path(hdfsConfPath));
			
			FileSystem fs = FileSystem.get(config);
			Path defaultDir = new Path(fs.getHomeDirectory(),".samoa");
			return defaultDir.toString();
		}
		
		static boolean deleteFileIfExist(String absPath) {
			Path p = new Path(absPath);
			return deleteFileIfExist(p);
		}
		
		static boolean deleteFileIfExist(Path p) {
			Configuration config = new Configuration();
			config.addResource(new Path(coreConfPath));
			config.addResource(new Path(hdfsConfPath));
			
			FileSystem fs;
			try {
				fs = FileSystem.get(config);
				if (fs.exists(p)) {
					return fs.delete(p, false);
				}
				else 
					return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		/*
		 * Write to HDFS
		 */
		static String writeToHDFS(File file, String dstPath) {
			Configuration config = new Configuration();
			config.addResource(new Path(coreConfPath));
			config.addResource(new Path(hdfsConfPath));
			logger.info("Filesystem name:{}",config.get("fs.defaultFS"));
			
			// Default samoaDir
			if (samoaDir == null) {
				try {
					samoaDir = getDefaultSAMOADir();
				}
				catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			// Setup src and dst paths
			//java.nio.file.Path tempPath = FileSystems.getDefault().getPath(samoaDir, dstPath);
			Path dst = new Path(samoaDir,dstPath);
			Path src = new Path(file.getAbsolutePath());
			
			// Delete file if already exists in HDFS
			if (deleteFileIfExist(dst) == false)
				return null;
			
			// Copy to HDFS
			FileSystem fs;
			try {
				fs = FileSystem.get(config);
				fs.copyFromLocalFile(src, dst);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			return dst.toString(); // abs path to file
		}
		
		/*
		 * Read from HDFS
		 */
		static Object deserializeObjectFromFile(String filePath) {
			logger.info("Deserialize HDFS file:{}",filePath);
			Configuration config = new Configuration();
			config.addResource(new Path(coreConfPath));
			config.addResource(new Path(hdfsConfPath));
			
			Path file = new Path(filePath);
			FSDataInputStream dataInputStream = null;
			ObjectInputStream ois = null;
			Object obj = null;
			FileSystem fs;
			try {
				fs = FileSystem.get(config);
				dataInputStream = fs.open(file);
				ois = new ObjectInputStream(dataInputStream);
				obj = ois.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				try {
					if (dataInputStream != null) dataInputStream.close();
					if (ois != null) ois.close();
				} catch (IOException ioException) {
					// TODO auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return obj;
		}
		
	}
	
	private static class LocalFileSystemUtils {
		static boolean serializObjectToFile(Object obj, String fn) {
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				fos = new FileOutputStream(fn);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(obj);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (fos != null) fos.close();
					if (oos != null) oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return true;
		}
	
		static Object deserializeObjectFromLocalFile(String filename) {
			logger.info("Deserialize local file:{}",filename);
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			Object obj = null;
			try {
				fis = new FileInputStream(filename);
				ois = new ObjectInputStream(fis);
				obj = ois.readObject();
			} catch (IOException e) {
				// TODO auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (fis != null) fis.close();
					if (ois != null) ois.close();
				} catch (IOException e) {
					// TODO auto-generated catch block
					e.printStackTrace();
				}
			}

			return obj;
		}
	}
	
	
	
	/*
	 * Create streams
	 */
	public static void createKafkaTopic(String name, int partitions) {
		createKafkaTopic(name, partitions, 1);
	}
	
	public static void createKafkaTopic(String name, int partitions, int replicas) {
		KafkaUtils.createKafkaTopic(name, partitions, replicas);
	}
	
	/*
	 * Serialize object
	 */
	public static boolean serializeObjectToLocalFileSystem(Object object, String path) {
		return LocalFileSystemUtils.serializObjectToFile(object, path);
	}
	
	public static String serializeObjectToHDFS(Object object, String path) {
		File tmpDatFile;
		try {
			tmpDatFile = File.createTempFile(TEMP_FILE, TEMP_FILE_SUFFIX);
			if (serializeObjectToLocalFileSystem(object, tmpDatFile.getAbsolutePath())) {
				return HDFSUtils.writeToHDFS(tmpDatFile, path);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Deserialize object
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> deserializeMapFromFile(String filesystem, String filename) {
		Map<String,Object> map;
		if (filesystem.equals(HDFS)) {
			map = (Map<String,Object>) HDFSUtils.deserializeObjectFromFile(filename);
		}
		else {
			map = (Map<String,Object>) LocalFileSystemUtils.deserializeObjectFromLocalFile(filename);
		}
		return map;
	}
	
	public static Object deserializeObjectFromFileAndKey(String filesystem, String filename, String key) {
		Map<String,Object> map = deserializeMapFromFile(filesystem, filename);
		if (map == null) return null;
		return map.get(key);
	}
	
	/*
	 * Setup
	 */
	public static void setZookeeper(String zookeeper) {
		KafkaUtils.setZookeeper(zookeeper);
	}
	
	public static void setHadoopConfigHome(String hadoopHome) {
		HDFSUtils.setHadoopConfigHome(hadoopHome);
	}
	
	public static void setSAMOADir(String samoaDir) {
		HDFSUtils.setSAMOADir(samoaDir);
	}
	
	/*
	 * Others
	 */
	public static String getHDFSNameNodeUri() {
		return HDFSUtils.getNameNodeUri();
	}
	public static String getHadoopConfigHome() {
		return HDFSUtils.getHadoopConfigHome();
	}
	
	public static String copyToHDFS(File file, String dstPath) {
		return HDFSUtils.writeToHDFS(file, dstPath);
	}
}
