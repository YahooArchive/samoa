package com.yahoo.labs.samoa.utils;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Utils class for building and deploying applications programmatically.
 * @author severien
 *
 */
public class Utils {

	public static void buildSamoaPackage() {
		try {
			String output = "/tmp/samoa/samoa.jar";// System.getProperty("user.home") + "/samoa.jar";
			Manifest manifest = createManifest();

			BufferedOutputStream bo;

			bo = new BufferedOutputStream(new FileOutputStream(output));
			JarOutputStream jo = new JarOutputStream(bo, manifest);

			String baseDir = System.getProperty("user.dir");
			System.out.println(baseDir);
			
			File samoaJar = new File(baseDir+"/target/samoa-0.0.1-SNAPSHOT.jar");
			addEntry(jo,samoaJar,baseDir+"/target/","/app/");
			addLibraries(jo);
			
			jo.close();
			bo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// TODO should get the modules file from the parameters
	public static void buildModulesPackage(List<String> modulesNames) {
		System.out.println(System.getProperty("user.dir"));
		try {
			String baseDir = System.getProperty("user.dir");
			List<File> filesArray = new ArrayList<>();
			for (String module : modulesNames) {
				module = "/"+module.replace(".", "/")+".class";
				filesArray.add(new File(baseDir+module));
			}
			String output = System.getProperty("user.home") + "/modules.jar";

			Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,
					"1.0");
			manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_URL,
					"http://samoa.yahoo.com");
			manifest.getMainAttributes().put(
					Attributes.Name.IMPLEMENTATION_VERSION, "0.1");
			manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR,
					"Yahoo");
			manifest.getMainAttributes().put(
					Attributes.Name.IMPLEMENTATION_VENDOR_ID, "SAMOA");

			BufferedOutputStream bo;

			bo = new BufferedOutputStream(new FileOutputStream(output));
			JarOutputStream jo = new JarOutputStream(bo, manifest);

			File[] files = filesArray.toArray(new File[filesArray.size()]);
			addEntries(jo,files, baseDir, "");

			jo.close();
			bo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void addLibraries(JarOutputStream jo) {
		try {
			String baseDir = System.getProperty("user.dir");
			String libDir = baseDir+"/target/lib";
			File inputFile = new File(libDir);
			
			File[] files = inputFile.listFiles();
			for (File file : files) {
				addEntry(jo, file, baseDir, "lib");
			}
			jo.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void addEntries(JarOutputStream jo, File[] files, String baseDir, String rootDir){
		for (File file : files) {

			if (!file.isDirectory()) {
				addEntry(jo, file, baseDir, rootDir);
			} else {
				File dir = new File(file.getAbsolutePath());
				addEntries(jo, dir.listFiles(), baseDir, rootDir);
			}
		}
	}
	
	private static void addEntry(JarOutputStream jo, File file, String baseDir, String rootDir) {
		try {
			BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file));

			String path = file.getAbsolutePath().replaceFirst(baseDir, rootDir);
			jo.putNextEntry(new ZipEntry(path));

			byte[] buf = new byte[1024];
			int anz;
			while ((anz = bi.read(buf)) != -1) {
				jo.write(buf, 0, anz);
			}
			bi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Manifest createManifest() {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_URL, "http://samoa.yahoo.com");
		manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VERSION, "0.1");
		manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR, "Yahoo");
		manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR_ID, "SAMOA");
		Attributes s4Attributes = new Attributes();
		s4Attributes.putValue("S4-App-Class", "path.to.Class");
		Attributes.Name name = new Attributes.Name("S4-App-Class");
		Attributes.Name S4Version = new Attributes.Name("S4-Version");
		manifest.getMainAttributes().put(name, "samoa.topology.impl.DoTaskApp");
		manifest.getMainAttributes().put(S4Version, "0.6.0-incubating");
		return manifest;
	}
	
	public static Object getInstance(String className) {
    Class<?> cls;
		Object obj = null;
		try {
			cls = Class.forName(className); 
			obj = cls.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
