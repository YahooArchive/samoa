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

import java.io.ByteArrayOutputStream;

import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Implementation of Samza's SerdeFactory
 * that uses Kryo to serialize/deserialize objects
 * 
 * @author Anh Thu Vu
 * @param <T>
 *
 */
public class SamzaKryoSerdeFactory<T> implements SerdeFactory<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(SamzaKryoSerdeFactory.class);
	
	public static class SamzaKryoSerde<V> implements Serde<V> {
		private Kryo kryo;
		
		public SamzaKryoSerde (String registrationInfo) {
			this.kryo = new Kryo();
			this.register(registrationInfo);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private void register(String registrationInfo) {
			if (registrationInfo == null) return;
			
			String[] infoList = registrationInfo.split(SamzaConfigFactory.COMMA);
			
			Class targetClass = null;
			Class serializerClass = null;
			Serializer serializer = null;
			
			for (String info:infoList) {
				String[] fields = info.split(SamzaConfigFactory.COLON);
				
				targetClass = null;
				serializerClass = null;
				if (fields.length > 0) {
					try {
						targetClass = Class.forName(fields[0].replace(SamzaConfigFactory.QUESTION_MARK, SamzaConfigFactory.DOLLAR_SIGN));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (fields.length > 1) {
					try {
						serializerClass = Class.forName(fields[1].replace(SamzaConfigFactory.QUESTION_MARK, SamzaConfigFactory.DOLLAR_SIGN));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (targetClass != null) {
					if (serializerClass == null) {
						kryo.register(targetClass);
					}
					else {
						serializer = resolveSerializerInstance(kryo, targetClass, (Class<? extends Serializer>)serializerClass) ;
						kryo.register(targetClass, serializer);
					}
				}
				else {
					logger.info("Invalid registration info:{}",info);
				}
			}
		}
		
		@SuppressWarnings("rawtypes")
		private static Serializer resolveSerializerInstance(Kryo k, Class superClass, Class<? extends Serializer> serializerClass) {
	        try {
	            try {
	                return serializerClass.getConstructor(Kryo.class, Class.class).newInstance(k, superClass);
	            } catch (Exception ex1) {
	                try {
	                    return serializerClass.getConstructor(Kryo.class).newInstance(k);
	                } catch (Exception ex2) {
	                    try {
	                        return serializerClass.getConstructor(Class.class).newInstance(superClass);
	                    } catch (Exception ex3) {
	                        return serializerClass.newInstance();
	                    }
	                }
	            }
	        } catch (Exception ex) {
	            throw new IllegalArgumentException("Unable to create serializer \""
	                                               + serializerClass.getName()
	                                               + "\" for class: "
	                                               + superClass.getName(), ex);
	        }
	    }
		
		/*
		 * Implement Samza Serde interface
		 */
		@Override
		public byte[] toBytes(V obj) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Output output = new Output(bos);
			kryo.writeClassAndObject(output, obj);
			output.flush();
			output.close();
			return bos.toByteArray();
		}

		@SuppressWarnings("unchecked")
		@Override
		public V fromBytes(byte[] byteArr) {
			Input input = new Input(byteArr);
			Object obj = kryo.readClassAndObject(input);
			input.close();
			return (V)obj;
		}
		
	}

	@Override
	public Serde<T> getSerde(String name, Config config) {
		return new SamzaKryoSerde<T>(config.get(SamzaConfigFactory.SERDE_REGISTRATION_KEY));
	}
}
