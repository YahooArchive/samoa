package com.yahoo.labs.samoa.topology.impl;

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

import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.util.SinglePartitionWithoutOffsetsSystemAdmin;

import com.yahoo.labs.samoa.topology.impl.SamzaEntranceProcessingItem.SamoaSystemConsumer;

/**
 * Implementation of Samza's SystemFactory
 * Samza will use this factory to get our custom consumer
 * which gets the events from SAMOA EntranceProcessor
 * and feed them to EntranceProcessingItem task
 * 
 * @author Anh Thu Vu
 */
public class SamoaSystemFactory implements SystemFactory {
	@Override
	public SystemAdmin getAdmin(String systemName, Config config) {
		return new SinglePartitionWithoutOffsetsSystemAdmin();
	}

	@Override
	public SystemConsumer getConsumer(String systemName, Config config, MetricsRegistry registry) {
		return new SamoaSystemConsumer(systemName, config);
	}

	@Override
	public SystemProducer getProducer(String systemName, Config config, MetricsRegistry registry) {
		throw new SamzaException("This implementation is not supposed to produce anything.");
	}
}