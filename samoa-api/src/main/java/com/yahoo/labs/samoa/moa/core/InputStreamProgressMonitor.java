package com.yahoo.labs.samoa.moa.core;

/*
 * #%L
 * SAMOA
 * %%
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Class for monitoring the progress of reading an input stream.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class InputStreamProgressMonitor extends FilterInputStream implements Serializable {

	/** The number of bytes to read in total */
	protected int inputByteSize;

	/** The number of bytes read so far */
	protected int inputBytesRead;
      
	public InputStreamProgressMonitor(InputStream in) {
		super(in);
		try {
			this.inputByteSize = in.available();
		} catch (IOException ioe) {
			this.inputByteSize = 0;
		}
		this.inputBytesRead = 0;
	}
        
	public int getBytesRead() {
		return this.inputBytesRead;
	}

	public int getBytesRemaining() {
		return this.inputByteSize - this.inputBytesRead;
	}

	public double getProgressFraction() {
		return ((double) this.inputBytesRead / (double) this.inputByteSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int c = this.in.read();
		if (c > 0) {
			this.inputBytesRead++;
		}
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int numread = this.in.read(b);
		if (numread > 0) {
			this.inputBytesRead += numread;
		}
		return numread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int numread = this.in.read(b, off, len);
		if (numread > 0) {
			this.inputBytesRead += numread;
		}
		return numread;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		long numskip = this.in.skip(n);
		if (numskip > 0) {
			this.inputBytesRead += numskip;
		}
		return numskip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilterInputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		this.in.reset();
		this.inputBytesRead = this.inputByteSize - this.in.available();
	}

}
