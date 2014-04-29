package com.yahoo.labs.samoa.topology.impl;

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

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.s4.core.util.AppConfig;
import org.apache.s4.core.util.ParsingUtils;
import org.apache.s4.deploy.DeploymentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.labs.samoa.tasks.Task;
import com.yahoo.labs.samoa.topology.ISubmitter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

public class S4Submitter implements ISubmitter {

	private static Logger logger = LoggerFactory.getLogger(S4Submitter.class);

	@Override
	public void deployTask(Task task) {
		// TODO: Get application FROM HTTP server
		// TODO: Initializa a http server to serve the app package
		
		String appURIString = null;
//		File app = new File(System.getProperty("user.dir")
//				+ "/src/site/dist/SAMOA-S4-0.1-dist.jar");
		
		// TODO: String app url http://localhost:8000/SAMOA-S4-0.1-dist.jar
		try {
			URL appURL = new URL("http://localhost:8000/SAMOA-S4-0.1.jar");
			appURIString = appURL.toString();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
//		try {
//			appURIString = app.toURI().toURL().toString();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
		if (task == null) {
			logger.error("Can't execute since evaluation task is not set!");
			return;
		} else {
			logger.info("Deploying SAMOA S4 task [{}] from location [{}]. ",
					task.getClass().getSimpleName(), appURIString);
		}

		String[] args = { "-c=testCluster2",
				"-appClass=" + S4DoTask.class.getName(),
				"-appName=" + "samoaApp",
				"-p=evalTask=" + task.getClass().getSimpleName(),
				"-zk=localhost:2181", "-s4r=" + appURIString , "-emc=" + SamoaSerializerModule.class.getName()};
		// "-emc=" + S4MOAModule.class.getName(),
		// "@" +
		// Resources.getResource("s4moa.properties").getFile(),

		S4Config s4config = new S4Config();
		JCommander jc = new JCommander(s4config);
		jc.parse(args);

		Map<String, String> namedParameters = new HashMap<String, String>();
		for (String parameter : s4config.namedParameters) {
			String[] param = parameter.split("=");
			namedParameters.put(param[0], param[1]);
		}

		AppConfig config = new AppConfig.Builder()
				.appClassName(s4config.appClass).appName(s4config.appName)
				.appURI(s4config.appURI).namedParameters(namedParameters)
				.build();

		DeploymentUtils.initAppConfig(config, s4config.clusterName, true,
				s4config.zkString);

		System.out.println("Suposedly deployed on S4");
	}

	
	public void initHTTPServer() {
		
	}
	
	@Parameters(separators = "=")
	public static class S4Config {

		@Parameter(names = { "-c", "-cluster" }, description = "Cluster name", required = true)
		String clusterName = null;

		@Parameter(names = "-appClass", description = "Main App class", required = false)
		String appClass = null;

		@Parameter(names = "-appName", description = "Application name", required = false)
		String appName = null;

		@Parameter(names = "-s4r", description = "Application URI", required = false)
		String appURI = null;

		@Parameter(names = "-zk", description = "ZooKeeper connection string", required = false)
		String zkString = null;

		@Parameter(names = { "-extraModulesClasses", "-emc" }, description = "Comma-separated list of additional configuration modules (they will be instantiated through their constructor without arguments).", required = false)
		List<String> extraModules = new ArrayList<String>();

		@Parameter(names = { "-p", "-namedStringParameters" }, description = "Comma-separated list of inline configuration "
				+ "parameters, taking precedence over homonymous configuration parameters from configuration files. "
				+ "Syntax: '-p=name1=value1,name2=value2 '", required = false, converter = ParsingUtils.InlineConfigParameterConverter.class)
		List<String> namedParameters = new ArrayList<String>();

	}

	@Override
	public void setLocal(boolean bool) {
		// TODO S4 works the same for local and distributed environments
	}
}
