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

import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
	private static final int NO_QUEUE_THRESHOLD = -1; // Negative value = no threshold
	private int queueSizeThreshold; // Negative value = no threshold
	private AtomicLong sequence = new AtomicLong(0);
	
	public PriorityThreadPoolExecutor() {
		this(NO_QUEUE_THRESHOLD);
	}
	
	public PriorityThreadPoolExecutor(int threshold) {
		this(1, 1, 10, TimeUnit.SECONDS, threshold);
	}
	
	public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit) {
		this(corePoolSize, maximumPoolSize, keepAliveTime, unit, NO_QUEUE_THRESHOLD);
	}
	
	public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, int threshold) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<Runnable>());
		this.queueSizeThreshold = threshold;
	}
	
	@Override
	protected <V> RunnableFuture<V> newTaskFor(Runnable runnable, V value)
    {
        return new ComparableFutureTask<V>(runnable, value);
    }
	
	// Just an estimation since we don't need a strict limit 
	// of the queue size
	public int getQueueSize() {
		return this.getQueue().size();
	}
	
	public boolean isQueueFull() {
		if (this.queueSizeThreshold < 0) return false;
		return this.getQueueSize() >= this.queueSizeThreshold;
	}
	
	public long nextSequenceNumber() {
		return sequence.getAndIncrement();
	}
	
	protected class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>> {
		private Object object;
		public ComparableFutureTask(Runnable runnable, T result) {
			super(runnable, result);
			object = runnable;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compareTo(ComparableFutureTask<T> o) {
			if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1; // this has higher priority than null
            }
            if (object != null && o.object != null) {
                if (object.getClass().equals(o.object.getClass())) {
                    if (object instanceof Comparable) {
                        return ((Comparable) object).compareTo(o.object);
                    }
                }
            }
            return 0;
		}
		
	}
}
